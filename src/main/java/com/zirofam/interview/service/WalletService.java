package com.zirofam.interview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zirofam.interview.config.AppConstants;
import com.zirofam.interview.config.KafkaConstants;
import com.zirofam.interview.config.RedisConstants;
import com.zirofam.interview.controller.model.FinancialModel;
import com.zirofam.interview.domain.WalletEntity;
import com.zirofam.interview.domain.enumeration.AccountingStatus;
import com.zirofam.interview.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Service
@Transactional
public class WalletService {

    @Value("${max.amount}")
    int maxAmount;

    @Value("${min.balance}")
    int minBalance;

    @Value("${max.balance}")
    int maxBalance;

    @Value("${redis.key.expiry-in-hours}")
    private int redisKeyExpiryInHours;

    @Autowired
    private final WalletRepository walletRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public WalletEntity findByUser(String user) {
        return walletRepository.findByUser(user);
    }

    public WalletEntity createWallet(String user) {
        WalletEntity wallet = WalletEntity.builder()
                .user(user)
                .balance(new BigDecimal(minBalance))
                .creationDate(new Date().toString())
                .build();
        return walletRepository.save(wallet);
    }

    public void updateWallet(final FinancialModel request) throws JsonProcessingException {

        JSONObject walletRequest = new JSONObject();
        walletRequest.put(AppConstants.USER, request.getUser());
        walletRequest.put(AppConstants.AMOUNT, request.getAmount());
        walletRequest.put(AppConstants.STATUS, request.getStatus());
        walletRequest.put(AppConstants.FINANCIAL_ID, request.getId());

        kafkaTemplate.send(KafkaConstants.KAFKA_UPDATE_WALLET_TOPIC,
                objectMapper.writeValueAsString(walletRequest));
    }

    @KafkaListener(topics = {KafkaConstants.KAFKA_UPDATE_WALLET_TOPIC},
            groupId = KafkaConstants.KAFKA_UPDATE_WALLET_TOPIC_GROUP_ID)
    public void updateWallet(final String event) throws JsonProcessingException {

        JSONObject updateWalletRequest = objectMapper.readValue(event, JSONObject.class);
        String status = (String) updateWalletRequest.get(AppConstants.STATUS);
        BigDecimal amount = new BigDecimal(Double.valueOf(updateWalletRequest.get(AppConstants.AMOUNT).toString()));
        String user = (String) updateWalletRequest.get(AppConstants.USER);

        WalletEntity wallet = walletRepository.findByUser(user);

        //Check all business rules about financial record are correct
        if(wallet != null && checkCondition(status, wallet.getBalance(), amount)) {

            Map userMap = redisTemplate.opsForHash().entries(RedisConstants.REDIS_KEY_PREFIX + user);

            //Check user has a record in processing
            if(userMap == null || userMap.size() == 0) {

                //Add user to redis for lock
                cacheInRedis(user);

                if (status.equals(AccountingStatus.CREDITOR.name())) {
                    //Execute wallet updating
                    walletRepository.incrementWallet(user, amount);
                }
                if (status.equals(AccountingStatus.DEBTOR.name())) {
                    //Execute wallet updating
                    walletRepository.decrementWallet(user, amount);
                }

                //Remove user from redis after execution
                String redisKey = RedisConstants.REDIS_KEY_PREFIX + user;
                redisTemplate.opsForHash().delete(redisKey);
            }
        }
    }

    private boolean checkCondition(String status, BigDecimal balance, BigDecimal amount) {
        //Max values for each request is maxAmount
        if (amount.compareTo(new BigDecimal(maxAmount)) == 1) {
            return false;
        }

        //MAx values for balance is maxBalance and Min values for balance is minBalance
        if (status.equals(AccountingStatus.CREDITOR.name())) {
            if (balance.add(amount).compareTo(new BigDecimal(maxBalance)) == 1 ||
                    balance.add(amount).compareTo(new BigDecimal(minBalance)) == -1) {
                return false;
            }
        }
        if (status.equals(AccountingStatus.DEBTOR.name())) {
            if (balance.subtract(amount).compareTo(new BigDecimal(maxBalance)) == 1 ||
                    balance.subtract(amount).compareTo(new BigDecimal(minBalance)) == -1) {
                return false;
            }
        }

        return true;
    }

    public void cacheInRedis(String user){
        String redisKey = RedisConstants.REDIS_KEY_PREFIX + user;
        Map userMap = objectMapper.convertValue(user, Map.class);
        redisTemplate.opsForHash().putAll(redisKey, userMap);
        redisTemplate.expire(redisKey, Duration.ofHours(redisKeyExpiryInHours));
    }
}

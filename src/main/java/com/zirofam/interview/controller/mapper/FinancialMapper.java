package com.zirofam.interview.controller.mapper;


import com.zirofam.interview.controller.dto.FinancialDto;
import com.zirofam.interview.controller.model.FinancialModel;
import com.zirofam.interview.domain.FinancialEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {})
public interface FinancialMapper {

    FinancialEntity toEntity(FinancialDto dto);

    FinancialModel toModel(FinancialEntity entity);

    List<FinancialModel> toModel(List<FinancialEntity> entity);
}

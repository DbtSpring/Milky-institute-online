package com.tianji.learning.pojo.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.utils.CollUtils;
import com.tianji.learning.pojo.entity.LearningLesson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "学习计划分页统计结果")
public class LearningPlanPageVO extends PageDTO<LearningPlanVO> {
    @ApiModelProperty("本周积分值")
    private Integer weekPoints;
    @ApiModelProperty("本周完成的计划数量")
    private Integer weekFinished;
    @ApiModelProperty("总的计划学习数量")
    private Integer weekTotalPlan;

    public LearningPlanPageVO() {
    }

    public LearningPlanPageVO emptyPage(Page<LearningLesson> p){
        this.setTotal(p.getTotal());
        this.setPages(p.getPages());
        this.setList(CollUtils.emptyList());
        return this;
    }

    public LearningPlanPageVO pageInfo(Long total, Long pages, List<LearningPlanVO> list) {
        this.total = total;
        this.pages = pages;
        this.list = list;
        return this;
    }

    public LearningPlanPageVO pageInfo(PageDTO<LearningPlanVO> pageDTO) {
        this.total = pageDTO.getTotal();
        this.pages = pageDTO.getPages();
        this.list = pageDTO.getList();
        return this;
    }

}
package com.macro.mall.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 售后处理记录Example类，用于条件查询
 */
public class OmsAfterSaleProcessExample {
    protected String orderByClause;
    protected boolean distinct;
    protected List<Criteria> oredCriteria;

    public OmsAfterSaleProcessExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdIsNull() {
            addCriterion("after_sale_id is null");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdIsNotNull() {
            addCriterion("after_sale_id is not null");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdEqualTo(Long value) {
            addCriterion("after_sale_id =", value, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdNotEqualTo(Long value) {
            addCriterion("after_sale_id <>", value, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdGreaterThan(Long value) {
            addCriterion("after_sale_id >", value, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdGreaterThanOrEqualTo(Long value) {
            addCriterion("after_sale_id >=", value, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdLessThan(Long value) {
            addCriterion("after_sale_id <", value, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdLessThanOrEqualTo(Long value) {
            addCriterion("after_sale_id <=", value, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdIn(List<Long> values) {
            addCriterion("after_sale_id in", values, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdNotIn(List<Long> values) {
            addCriterion("after_sale_id not in", values, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdBetween(Long value1, Long value2) {
            addCriterion("after_sale_id between", value1, value2, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andAfterSaleIdNotBetween(Long value1, Long value2) {
            addCriterion("after_sale_id not between", value1, value2, "afterSaleId");
            return (Criteria) this;
        }

        public Criteria andProcessTypeIsNull() {
            addCriterion("process_type is null");
            return (Criteria) this;
        }

        public Criteria andProcessTypeIsNotNull() {
            addCriterion("process_type is not null");
            return (Criteria) this;
        }

        public Criteria andProcessTypeEqualTo(Integer value) {
            addCriterion("process_type =", value, "processType");
            return (Criteria) this;
        }

        public Criteria andProcessTypeNotEqualTo(Integer value) {
            addCriterion("process_type <>", value, "processType");
            return (Criteria) this;
        }

        public Criteria andProcessTypeIn(List<Integer> values) {
            addCriterion("process_type in", values, "processType");
            return (Criteria) this;
        }

        public Criteria andProcessTypeNotIn(List<Integer> values) {
            addCriterion("process_type not in", values, "processType");
            return (Criteria) this;
        }

        public Criteria andProcessResultIsNull() {
            addCriterion("process_result is null");
            return (Criteria) this;
        }

        public Criteria andProcessResultIsNotNull() {
            addCriterion("process_result is not null");
            return (Criteria) this;
        }

        public Criteria andProcessResultEqualTo(Integer value) {
            addCriterion("process_result =", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultNotEqualTo(Integer value) {
            addCriterion("process_result <>", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultIn(List<Integer> values) {
            addCriterion("process_result in", values, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultNotIn(List<Integer> values) {
            addCriterion("process_result not in", values, "processResult");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;
        private Object value;
        private Object secondValue;
        private boolean noValue;
        private boolean singleValue;
        private boolean betweenValue;
        private boolean listValue;
        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
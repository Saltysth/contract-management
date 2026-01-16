-- ============================================================================
-- V5: Performance Indexes and Initial Data
-- 性能优化索引和初始数据
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. 性能优化索引
-- ----------------------------------------------------------------------------

-- 任务查询优化
CREATE INDEX idx_task_status_type ON task(task_status, task_type);
CREATE INDEX idx_task_created_by_status ON task(created_by, task_status);
CREATE INDEX idx_task_status_stage ON task(task_status, current_stage);

-- 合同任务查询优化
CREATE INDEX idx_contract_task_contract_review ON contract_task(contract_id, review_type);

-- 审查结果查询优化
CREATE INDEX idx_review_result_contract_type ON review_result(contract_id, review_type);
CREATE INDEX idx_review_result_risk_created ON review_result(overall_risk_level, created_time DESC);

-- 条款查询优化
CREATE INDEX idx_clause_contract_type ON clause(contract_id, clause_type);
CREATE INDEX idx_clause_type_risk ON clause(clause_type, risk_level);

-- 操作日志查询优化
CREATE INDEX idx_operation_log_user_type ON operation_log(user_id, operation_type);
CREATE INDEX idx_operation_log_resource_created ON operation_log(resource_type, resource_id, created_time DESC);

-- 为clause_extractions表添加性能优化索引
CREATE INDEX idx_clause_extraction_contract_status ON clause_extractions(contract_id, status);

-- ----------------------------------------------------------------------------
-- 2. 审查规则初始数据
-- ----------------------------------------------------------------------------
INSERT INTO contract_review_rule (rule_name, rule_type, applicable_contract_type, applicable_clause_type, rule_content, prompt_mode_code, enabled, category, remark) VALUES
-- 风险评估类规则 (RISK_ASSESSMENT)
('支付风险评估', 'specific', ARRAY['SALES', 'PURCHASE'], ARRAY['payment_term'], '评估付款条件是否合理，是否存在资金风险', 1, true, 'RISK_ASSESSMENT', '评估付款周期、付款方式等风险因素'),
('违约责任风险评估', 'specific', ARRAY['SALES', 'PURCHASE', 'SERVICE'], ARRAY['liability_clause', 'breach_clause'], '检查违约责任条款是否公平，责任划分是否清晰', 2, true, 'RISK_ASSESSMENT', '重点审查违约金比例、责任限额等'),
('合同有效期风险评估', 'fallback', ARRAY['ALL'], ARRAY['term_clause'], '评估合同期限是否合理，是否存在期限过长或过短风险', 1, true, 'RISK_ASSESSMENT', '检查固定期限、续约条件等'),
('保密条款风险评估', 'specific', ARRAY['SERVICE', 'EMPLOYMENT'], ARRAY['confidentiality_clause'], '评估保密范围、保密期限是否适当', 2, true, 'RISK_ASSESSMENT', '检查保密信息的定义和保密义务'),
('知识产权风险评估', 'specific', ARRAY['SERVICE', 'LEASE'], ARRAY['ip_clause'], '评估知识产权归属、使用权限是否明确', 2, true, 'RISK_ASSESSMENT', '重点审查专利、商标、著作权等条款'),

-- 条款分析类规则 (CLAUSE_ANALYSIS)
('付款条款分析', 'fallback', ARRAY['ALL'], ARRAY['payment_term'], '分析付款时间、方式、币种等关键要素', 1, true, 'CLAUSE_ANALYSIS', '提取付款相关的所有关键信息'),
('交付条款分析', 'fallback', ARRAY['ALL'], ARRAY['delivery_clause'], '分析交付时间、地点、方式等要素', 1, true, 'CLAUSE_ANALYSIS', '识别交付相关的核心条款'),
('质量标准条款分析', 'fallback', ARRAY['ALL'], ARRAY['quality_clause'], '分析质量要求、检验标准等条款', 2, true, 'CLAUSE_ANALYSIS', '提取质量相关的所有标准'),
('不可抗力条款分析', 'fallback', ARRAY['ALL'], ARRAY['force_majeure_clause'], '分析不可抗力的定义、范围、处理方式', 2, true, 'CLAUSE_ANALYSIS', '识别不可抗力事件的类型和后果'),
('争议解决条款分析', 'fallback', ARRAY['ALL'], ARRAY['dispute_resolution'], '分析争议解决方式、管辖法院等', 2, true, 'CLAUSE_ANALYSIS', '识别诉讼、仲裁等解决途径'),

-- 完整性检查类规则 (COMPLETENESS_CHECK)
('必备条款完整性检查', 'fallback', ARRAY['ALL'], ARRAY['all'], '检查合同是否包含所有必备的基本条款', 1, true, 'COMPLETENESS_CHECK', '确保包含当事人、标的、价款等要素'),
('合同主体信息完整性', 'fallback', ARRAY['ALL'], ARRAY['party_info'], '检查合同双方的名称、地址、联系方式是否完整', 1, true, 'COMPLETENESS_CHECK', '验证签约主体的基本信息'),
('签字盖章完整性检查', 'fallback', ARRAY['ALL'], ARRAY['signature_clause'], '检查合同是否有完整的签字盖章页', 1, true, 'COMPLETENESS_CHECK', '确认生效条件是否满足'),
('合同编号完整性检查', 'fallback', ARRAY['ALL'], ARRAY['header_info'], '检查是否有规范的合同编号', 1, true, 'COMPLETENESS_CHECK', '确保合同档案管理规范'),
('生效条件完整性检查', 'fallback', ARRAY['ALL'], ARRAY['effective_clause'], '检查合同生效条件是否明确', 1, true, 'COMPLETENESS_CHECK', '确认合同何时开始生效'),

-- 合规检查类规则 (COMPLIANCE_CHECK)
('反垄断合规检查', 'specific', ARRAY['SALES', 'PURCHASE'], ARRAY['price_clause', 'market_clause'], '检查是否存在垄断、价格操纵等违法条款', 2, true, 'COMPLIANCE_CHECK', '确保符合反垄断法规定'),
('劳动法规合规检查', 'fallback', ARRAY['ALL'], ARRAY['employment_term'], '检查劳动合同是否符合劳动法规定', 2, true, 'COMPLIANCE_CHECK', '重点检查工作时间、社保等条款'),
('数据保护合规检查', 'specific', ARRAY['SERVICE'], ARRAY['data_protection_clause'], '检查个人信息处理是否符合GDPR等法规', 2, true, 'COMPLIANCE_CHECK', '确保数据收集、使用合法合规'),
('税务合规检查', 'fallback', ARRAY['ALL'], ARRAY['tax_clause'], '检查涉税条款是否符合税法规定', 2, true, 'COMPLIANCE_CHECK', '关注税率、发票等税务条款'),

-- 先例对比类规则 (PRECEDENT_COMPARISON)
('同类合同条款对比', 'fallback', ARRAY['ALL'], ARRAY['all'], '与同类合同的条款进行对比分析', 2, true, 'PRECEDENT_COMPARISON', '识别条款的差异性'),
('行业标准条款对比', 'fallback', ARRAY['ALL'], ARRAY['industry_specific'], '与行业标准的合同条款进行对比', 2, true, 'PRECEDENT_COMPARISON', '评估条款是否符合行业惯例'),
('历史版本对比', 'fallback', ARRAY['ALL'], ARRAY['all'], '与合同历史版本进行对比', 2, true, 'PRECEDENT_COMPARISON', '识别条款变更情况');

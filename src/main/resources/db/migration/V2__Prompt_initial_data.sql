-- ============================================================================
-- V2: Prompt Initial Data
-- 提示词模板初始数据
-- ============================================================================

INSERT INTO "public"."prompt" ("prompt_name", "prompt_type", "prompt_role", "prompt_content", "enabled", "response_format", "remark", "created_by", "updated_by", "created_time", "updated_time", "object_version_number")
VALUES
('分类合同提示词-文字', 'INNER', 'USER', '你是一个合同审查专家，你十分擅长进行合同的分类，请你根据我给你的合同片段来判断合同的类型，并用规定的json格式返回数据。
合同内容:
{CONTENT}

返回格式:
{
    "contractType": "采购合同/服务合同/租赁合同/劳动合同/合作协议/其他",
    "confidence": "置信度，精确到两位小数",
    "reason":"这里填写原因"
}', 't', 'json', NULL, 1, 1, '2025-11-18 16:16:38.178567', '2025-11-18 16:16:38.181121', 0),

('分类合同提示词-图片', 'INNER', 'USER', '你是一个合同审查专家，你十分擅长进行合同的分类，请你根据我给你的合同文件/图片来判断合同的类型，并用规定的json格式返回数据,禁止返回多余数据或者非json格式数据。


返回格式:
{
    "contractType": "采购合同/服务合同/租赁合同/劳动合同/合作协议/其他",
    "confidence": "置信度，精确到两位小数",
    "reason":"这里填写原因"
}', 't', 'json', NULL, 1, 1, '2025-11-18 16:17:34.965298', '2025-11-18 16:17:34.974792', 0),

('一体化条款抽取', 'INNER', 'USER', '请分析这个合同文档，完成以下任务：

1. **文档识别（OCR）**：
   - 提取所有文本内容
   - 保持原文档的页面和段落结构
   - 识别文档基本信息（合同双方、日期等）

2. **结构分析**：
   - 识别主要章节标题和页码
   - 标记表格、列表等特殊内容区域
   - 识别签字区域、附件等特殊部分
   - 分析文档整体逻辑结构

3. **条款抽取**：
   - 抽取合同条款内容
   - 对每个条款进行分类（付款条件、违约责任等）
   - 识别关键实体和数值信息
   - 评估条款的重要性和风险级别

4. **位置定位**：
   - 标记每个抽取条款在文档中的精确位置
   - 计算页码和边界框坐标
   - 提供上下文信息便于验证

**返回JSON格式**：
{
  "task_info": {
    "task_id": "unique_task_identifier",
    "processing_time": 180,
    "document_quality": "high"
  },

  "document_info": {
    "total_pages": 25,
    "document_type": "采购合同",
    "parties": {
      "party_a": "甲方：XXX科技有限公司",
      "party_b": "乙方：YYY设备制造有限公司"
    },
    "contract_date": "2024-01-15",
    "contract_amount": "1,500,000.00元"
  },

  "structure": {
    "chapters": [
      {
        "title": "第一条 合同标的",
        "page_start": 2,
        "page_end": 3,
        "level": 1,
        "subsections": [
          {"title": "1.1 设备清单", "page": 2}
        ]
      },
      {
        "title": "第二条 付款方式",
        "page_start": 4,
        "page_end": 5,
        "level": 1
      }
    ],
    "special_areas": [
      {"type": "table", "pages": [2], "description": "设备清单表"},
      {"type": "signature", "page": 25, "description": "签字区域"}
    ]
  },

  "clauses": [
    {
      "clause_id": "payment_001",
      "clause_type": "付款条件",
      "clause_title": "首期付款",
      "content": "合同签订后30日内支付合同总金额的30%作为首期付款。",
      "confidence_score": 0.95,
      "risk_level": "medium",
      "extracted_entities": {
        "amount_percentage": "30%",
        "payment_period": "30日",
        "payment_condition": "合同签订后",
        "payment_amount": "450,000.00元"
      },
      "risk_factors": [
        {"factor": "付款期限较长", "severity": "medium"},
        {"factor": "未明确逾期责任", "severity": "low"}
      ],
      "positions": [
        {
          "page": 4,
          "startIndex": 0,
          "endIndex": 0
        }
      ],
      "related_chapters": ["第二条 付款方式"],
      "extracted_at": "2024-01-15T10:30:00Z"
    }
  ],

  "quality_metrics": {
    "total_clauses_found": 15,
    "high_confidence_clauses": 12,
    "medium_confidence_clauses": 2,
    "low_confidence_clauses": 1,
    "pages_with_issues": [8, 16],
    "ocr_confidence_avg": 0.92,
    "structure_accuracy": 0.88,
    "recommended_review_clauses": ["payment_003", "liability_001"]
  }
}', 't', 'json', NULL, 1, 1, '2025-11-18 16:27:40.252945', '2025-11-20 15:41:34.215348', 1),

('其他合同提示词', 'INNER', 'USER', '角色定义
你是资深合同审查专家，精通《民法典》及各类民商事合同法律规定，具备 10 年以上合同审查经验，擅长识别合同条款中的法律风险、逻辑矛盾、表述模糊、必备要素缺失、违法违规等问题。核心能力要求：① 基于条款类型提供精准修正建议，需包含可直接替换的具体条款片段；② 严格遵循指定 Java 实体对应的 JSON 结构输出结果，无任何字段增减或格式调整；③ 依据必须明确引用具体法律条文（如《民法典》第 511 条）或审查规则，不得遗漏；④ 所有数值型字段按要求保留 2 位小数，文本类字段严格遵守长度限制。

输出要求（强制 JSON 格式）
请严格按照以下固定 JSON 结构输出，不得增减字段、改变字段类型、调整字段顺序，所有字段需完全符合注释要求：
{
    "overallRiskLevel": "MEDIUM", // 取值参考 RiskLevel
    "summary": "合同整体风险中等，主要关注付款条款和违约责任条款", // 进行合同层面的总结，不超过 50 字。
    "ruleResults": [
        {
            "riskName": "付款条款风险",
            "ruleType": "PAYMENT_TERM", // 取值参考 ReviewTypeDetail
            "riskLevel": "HIGH", // 取值参考 RiskLevel，根据 riskScore 决定
            "riskScore": 85.5, // 根据评分规则取值
            "summary": "付款期限过长，存在现金流风险", // 对 findings 进行的 30 字内总结。
            "findings": [
                "付款时间为发票开具后 90 天",
                "未约定延迟付款的违约金",
                "缺乏分期付款保障机制"
            ], // 值得注意的点或者风险点
            "recommendation": [
                "建议将付款期限缩短至 30-45 天",
                "增加延迟付款违约金条款",
                "考虑设置分期付款或预付款机制"
            ], //findings 每条对应的建议
            "riskClauseId": "1005,1006", // 对应的风险条款 id 列表
            "originContractText": "" // 如果并非条款内容，而是合同别处内容请在此处标注原文，否则为空
        }
    ],
    "keyPoints": [ // 整体取值于 ruleResults
        {
            "point": "付款期限约定为 90 天，远超行业标准 45 天", // 注重于条款的具体问题点
            "type": "逾期风险", //ruleResults.ruleType
            "remediationSuggestions": [
                "建议修改为 30 天内付款",
                "如无法修改，建议约定逾期付款的违约金（日千分之五）",
                "可考虑提供提前付款折扣"
            ], //ruleResults.recommendation
            "riskLevel": "HIGH", // 取值参考 RiskLevel
            "reviewRuleId": 101, // 相关审查规则 id
            "clauseIds": "1005,1006" // 相关条款 id
        }
    ],
    "evidences": [
        {
            "title": "终止条件",
            "type": "rule", // 取值于 EvidenceType
            "content": "对合同终止条件的评估。",
            "content": "终止条件列举清晰，涵盖了常见的终止情形。",
            "references": ["片段 1", "片段 2"]
        }
    ]
}

强制约束（严格执行，不得违反）
1. 严格遵循上述 JSON 结构输出，字段数量、类型、顺序完全一致，不得新增、删除或修改任何字段；
2. 字段值需符合对应注释要求：summary（合同层面总结）≤50 字，ruleResults.summary≤30 字，riskSummary（按原要求）≤10 字（超出需截断优化）；
3. 数值型字段（riskScore 等）必须保留 2 位小数，如 85.00、60.50，不得省略小数位；
4. 所有风险评估必须提供依据：优先引用具体法律条文（如《民法典》第 511 条），无法律条文则引用审查规则，不得遗漏依据说明；
5. 修正建议（recommendation、remediationSuggestions）需具体可落地，禁止 "明确 XXX""完善 XXX" 等笼统表述，必须提供可直接替换的条款片段；
6. 禁止使用表格、列表（JSON 内部数组除外）等形式，所有说明均以文字形式融入对应字段；
7. originContractText 字段：仅当风险内容来自合同非条款部分时填写原文，条款相关风险则留空；
8. 引用法律条文需准确，无对应条文时需明确标注 "依据合同审查规则第 XX 条"，确保依据可追溯。
9. 取值时大小写敏感

审查规则
</rules>

待审查条款
</clauses>

取值参考:
RiskLevel:
</RiskLevel>

ReviewTypeDetail:
</ReviewTypeDetail>

EvidenceType:
</EvidenceType>

本合同属于</industry>，如无明确说明则使用货币为</currency>
', 't', 'json', '系统内置的其他合同审查提示词', 1, 1, '2025-12-01 10:28:56.042133', '2025-12-01 10:52:22.530141', 2);

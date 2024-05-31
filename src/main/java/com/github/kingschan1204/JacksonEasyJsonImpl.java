package com.github.kingschan1204;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonEasyJsonImpl {

    ObjectMapper objectMapper;
    JsonNode root;

    private void init() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //支持解析单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    }

    private JacksonEasyJsonImpl() {
    }

    ;

    public JacksonEasyJsonImpl(Object obj) {
        if (obj instanceof String) {
            init();
            try {
                root = objectMapper.readTree(String.valueOf(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过
     * <p>属性. 的方式取 支持多层<p/>
     * <p>$first 返回jsonArray的第一个对象<p/>
     * <p>$last 返回jsonArray的最后一个对象<p/>
     * <p>* 返回jsonArray的所有对象<p/>
     * <p>,逗号分隔可获取jsonArray的多个字段组成新对象返回<p/>
     * <p>->arrayList抽取单个字段转成 arrayList类似 [1,2,3,4]<p/>
     *
     * @param expression 表达式  属性.属性
     * @param clazz      返回类型
     * @return 表达式的值
     */
    public <T> T get(String expression, Class<T> clazz) {
        String[] depth = expression.split("\\.");
        Object object = getValByExpression(root, depth[0]);
        for (int i = 1; i < depth.length; i++) {
            object = getValByExpression(object, depth[i]);
        }
        return (T) object;
    }

    private Object getValByExpression(Object object, String expression) {
        // jsonObject
        if (object instanceof ObjectNode) {
            return ((ObjectNode) object).get(expression);
        } else if (object instanceof ArrayNode) {
            //jsonArray模式
            //如果是数字直接取下标，保留关键字：$first第一条 $last最后一条
            ArrayNode js = (ArrayNode) object;
            if (expression.matches("\\d+")) {
                return js.get(Integer.parseInt(expression));
            } else if (expression.matches("\\$first")) {
                return js.get(0);
            } else if (expression.matches("\\$last")) {
                return js.get(js.size() - 1);
            }
            // 抽取单个字段转成 arrayList类似 [1,2,3,4]
            else if (expression.matches("\\w+(->)arrayList$")) {
                String key = expression.replace("->arrayList", "");
                ArrayNode list = new ObjectMapper().createArrayNode();
                for (int i = 0; i < js.size(); i++) {
                    list.add(js.get(i).get(key));
                }
                return list;
            } else if (expression.contains(",")) {
                //从集合里抽 支持多字段以,逗号分隔
                String[] fields = expression.split(",");
                ArrayNode result = new ObjectMapper().createArrayNode();
                for (int i = 0; i < js.size(); i++) {
                    ObjectNode json = new ObjectMapper().createObjectNode();
                    for (String key : fields) {
                        json.put(key, js.get(i).get(key));
                    }
                    result.add(json);
                }
                return result;
            }
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        String text = """
                {
                  "data": {
                    "market": {
                      "status_id": 7,
                      "region": "CN",
                      "status": "已收盘",
                      "time_zone": "Asia/Shanghai",
                      "time_zone_desc": null,
                      "delay_tag": 0
                    },
                    "tags": [
                      {
                        "description": "深股通",
                        "value": 3,
                        "o":"a"
                      },
                      {
                        "description": "融",
                        "value": 6,
                        "o":"b"
                      },
                      {
                        "description": "空",
                        "value": 7,
                        "o":"c"
                      }
                    ]
                  }
                }
                """;
      /*  ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(text);
        ArrayNode tags = (ArrayNode) root.get("data").get("tags");

        tags.remove(1);

        System.out.println(tags.getNodeType());
        System.out.println(root.getNodeType());
        System.out.println(tags);

        System.out.println(root instanceof ObjectNode);
        System.out.println(root.get("data").get("tags") instanceof ArrayNode);*/
        JacksonEasyJsonImpl easyJson = new JacksonEasyJsonImpl(text);
        System.out.println(easyJson.get("data.market.status_id", Integer.class));
        System.out.println(easyJson.get("data.tags.1", Object.class));
        System.out.println(easyJson.get("data.tags.value->arrayList", Object.class));
        System.out.println(easyJson.get("data.tags.value,o", Object.class));

    }
}

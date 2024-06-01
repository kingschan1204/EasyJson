import com.github.kingschan1204.EasyJson;
import dto.TagDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("json操作测试")
public class JsonTest {
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
    EasyJson easyJson = EasyJson.of(text);
    @DisplayName("各种查找方法")
    @Test
    public void getTest(){

        System.out.println(easyJson.get("data.market.status_id", Integer.class));
        System.out.println(easyJson.get("data.tags.1", Object.class));
        System.out.println(easyJson.get("data.tags.value->arrayList", Object.class));
        System.out.println(easyJson.get("data.tags.value,o", Object.class));
        System.out.println(easyJson.keySet());

        List<Integer> arrayList =  List.of(1, 2, 3, 4, 5, 6);
//        System.out.println(easyJson.put("aa","bb").put("arrays",arrayList));
    }

    @DisplayName("转换为对象的方法")
    @Test
    public void toObject(){
        System.out.println(easyJson.op("data.tags.1").toJavaObj(TagDto.class));
        System.out.println(easyJson.op("data.tags").toListObj(TagDto.class));
    }


    @DisplayName("新增元素的方法")
    @Test
    public void addTest(){
        //指定位置 新增元素
        System.out.println(easyJson
                .put("data.market","demo",666)
                .put("data.tags.0","demo",888)

        );
    }

}

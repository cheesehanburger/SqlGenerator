package com.hanburger.sqlfather.core.generator;

import com.hanburger.sqlfather.core.model.enums.MockParamsRandomTypeEnum;
import com.hanburger.sqlfather.core.schema.TableSchema.Field;
import com.hanburger.sqlfather.core.utils.FakerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 随机值数据生成器
 *
 * @author hanburger
 */
public class RandomDataGenerator implements DataGenerator {

    @Override
    public List<String> doGenerate(Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            // 将模拟数据类型转化为枚举类
            MockParamsRandomTypeEnum randomTypeEnum = Optional.ofNullable(
                            MockParamsRandomTypeEnum.getEnumByValue(mockParams))
                    .orElse(MockParamsRandomTypeEnum.STRING);
            // 通过随机数生成工具返回模拟数据
            String randomString = FakerUtils.getRandomValue(randomTypeEnum);
            list.add(randomString);
        }
        return list;
    }
}

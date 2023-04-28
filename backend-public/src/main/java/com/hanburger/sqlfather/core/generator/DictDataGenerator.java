package com.hanburger.sqlfather.core.generator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hanburger.sqlfather.common.ErrorCode;
import com.hanburger.sqlfather.core.schema.TableSchema.Field;
import com.hanburger.sqlfather.exception.BusinessException;
import com.hanburger.sqlfather.model.entity.Dict;
import com.hanburger.sqlfather.service.DictService;
import com.hanburger.sqlfather.utils.SpringContextUtils;
import org.apache.commons.lang3.RandomUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 词库数据生成器
 *
 * @author hanburger
 */
public class DictDataGenerator implements DataGenerator {

    private static final DictService dictService = SpringContextUtils.getBean(DictService.class);

    private final static Gson GSON = new Gson();


    @Override
    public List<String> doGenerate(Field field, int rowNum) {
        String mockParams = field.getMockParams();
        long id = Long.parseLong(mockParams);
        Dict dict = dictService.getById(id);
        if (dict == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "词库不存在");
        }
        List<String> wordList = GSON.fromJson(dict.getContent(),
                new TypeToken<List<String>>() {
                }.getType());
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            String randomStr = wordList.get(RandomUtils.nextInt(0, wordList.size()));
            list.add(randomStr);
        }
        return list;
    }
}

package com.sf.scs.gather.dto.typeInfo.factory;

import com.sf.scs.gather.dto.ColdWaybillDetail;
import com.sf.scs.gather.dto.Fvp;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 *  ID   DATE               PERSON             REASON
 *  1   2022/12/16 14:17     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public class ColdWaybillDetailTypeInfoFactory extends TypeInfoFactory<ColdWaybillDetail> {
    @Override
    public TypeInformation<ColdWaybillDetail> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {
        Map<String, TypeInformation<?>> fields = new HashMap<>();
        Class<ColdWaybillDetail> coldWaybillDetailClass = ColdWaybillDetail.class;
        Field[] declaredFields = coldWaybillDetailClass.getDeclaredFields();
        for(Field f:declaredFields){
            fields.put(f.getName(),TypeInformation.of(f.getType()));
        }
        fields.put("fvpList", Types.LIST(Types.POJO(Fvp.class)));
        fields.put("childWaybillNo",Types.LIST(Types.STRING));
        TypeInformation<ColdWaybillDetail> pojo = Types.POJO(ColdWaybillDetail.class,fields);
        return pojo;
    }
}
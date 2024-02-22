package cdc;

import java.util.List;

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 *  ID   DATE               PERSON             REASON
 *  1   2023/4/23 15:19     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public interface BatchUpdate<T> {

    public void add(T one);

    public List<T> getList();

    public UPDATE_TYPE getType();

    public String getSql();

    public enum UPDATE_TYPE{
        DATA,SQL;
    }
}

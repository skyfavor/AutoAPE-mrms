package com.seculayer.mrms.rest.servlet.select;

import com.seculayer.mrms.managers.MRMServerManager;
import com.seculayer.mrms.rest.ServletHandlerAbstract;
import com.seculayer.util.JsonUtil;
import com.seculayer.util.StringUtil;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class GetFieldUnique extends ServletHandlerAbstract {
    public static final String ContextPath = ServletHandlerAbstract.ContextPath + "/get_field_unique";

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletResponse.setContentType("text/json; charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();

        logger.debug("###################################################################");
        logger.debug("In doGet - get field unique");

        boolean flag = false;
        String datasetID = httpServletRequest.getParameter("dataset_id");
        String fieldName = httpServletRequest.getParameter("field_name");
        String jobDir = MRMServerManager.getInstance().getConfiguration().get("ape.da.dir");

        try {
            Map<String, Object> metaData = this.daMetaFileLoad(jobDir, datasetID);
            List<Map<String, Object>> listMap = (List<Map<String, Object>>) metaData.get("meta");

            for (Map<String, Object> map: listMap) {
                if (map.get("field_nm").toString().equals(fieldName)) {
                    Map<String, Object> statistics = (Map<String, Object>) map.get("statistics");
                    Map<String, Object> unique = (Map<String, Object>) statistics.get("unique");
                    JSONObject uniqueJson = JsonUtil.mapToJson(unique);
                    out.println(uniqueJson);
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("error");
        }
        if (!flag) {
            out.println("{}");
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        logger.debug("###################################################################");
    }

    private Map<String, Object> daMetaFileLoad(String jobDir, String datasetID) throws Exception {
        File file = new File(jobDir + "/" + datasetID, "DA_META_" + datasetID + ".info");

        Map<String, Object> map = JsonUtil.strToMap(
            JsonUtil.getJSONString(new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()))));

        return map;
    }
}

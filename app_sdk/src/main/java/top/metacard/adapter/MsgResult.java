package top.metacard.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class MsgResult {
    public MsgResult(String msg, String info, Map<String,Object> result){
        this.msg = msg;
        this.info = info;
        this.result = result;
    }
    public String msg = "ok";
    public String info = "success";
    public String err = ""; // 具体的错误信息
    public Object result = null;
    public String toJSON(){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return objectMapper.writeValueAsString(this);
        }catch (Exception e){
            return """
                    {
                        "msg":"json_err",
                        "info:"result object 打包成json失败!"
                    }
                    """;
        }
    }
    public static MsgResult errByDetail(String errMsg,String errInfo,String errorDetail){
        var m = new MsgResult(errMsg,errInfo,null);
        m.err = errorDetail;
        return m;
    }
    public static MsgResult err(String errInfo){
        return new MsgResult("err",errInfo,null);
    }
    public static MsgResult errMsg(String msg){
        return new MsgResult(msg,null,null);
    }
    public static MsgResult errMsg(String msg,String info){
        return new MsgResult(msg,info,null);
    }
    public static MsgResult ok(){
        return new MsgResult("ok","success",null);
    }
    public static MsgResult ok(Map<String,Object> result){
        return new MsgResult("ok","success",result);
    }
}

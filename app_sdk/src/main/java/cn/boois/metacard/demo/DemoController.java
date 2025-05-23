package cn.boois.metacard.demo;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import top.metacard.adapter.GetCardOrCreateAdapter;
import top.metacard.adapter.MsgResult;

class UserDTO {
    private String name;
    private int age;
    // getters/setters省略
}

@RestController
@RequestMapping("/api")
public class DemoController {

    @PostMapping("/get_card_or_create")
    public String getCardOrCreate(@RequestHeader Map<String, Object> headers,
            @RequestBody Map<String, Object> requestBody) {
        // 创建一个适配器
        GetCardOrCreateAdapter adapter = new GetCardOrCreateAdapter(headers, requestBody);
        // 初始化适配器,会自动处理客户端传过来的参数
        MsgResult r = adapter.init();
        // 初始化失败就直接返回结果
        if (!r.msg.equals("ok"))
            return r.toJSON();

        String userId = adapter.userId; // userId直接显式可用
        String cardId = adapter.cardId; // cardId直接显式可用
        String bin = adapter.bin; // bin直接显式可用

        /*
         * adapter.getValue("meta-key");// 先从Meta中获取,没找到再从Kyc数据中获取
         * adapter.getMetaValue("meta-key") //明确从Meta中获取
         * adapter.getKycValue("kyc-key") // 明确从Kyc数据中获取防止被覆盖
         * 开始处理具体的业务,可能出现的情况:
         * 1. 用户卡片存在,直接返回卡片数据 responseCardData
         * 2. 用户Kyc审核中,返回审核中的状态 responseKycPending
         * 3. 卡不存在,用用户的资料开卡,如果KYC不通过,返回补填的要求 responseKCYNotCompleted
         * 4. 卡不存在,开卡,成功,返回卡片数据 responseCardData
         * 5. 开卡失败,当前卡片超出了数量限制 responseCardCountLimit
         * 6. 未考虑到的错误 responseOtherError
         */

         // 准备返回的卡数据
        var wallets = new ArrayList<GetCardOrCreateAdapter.Wallet>();
        wallets.add(new GetCardOrCreateAdapter.Wallet(
                GetCardOrCreateAdapter.WalletType.ETH,
                "0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
                "-100.0"));
        // 增加fee的展示字段，可选
        var feeDisplayKVs = new ArrayList<GetCardOrCreateAdapter.feeKV>();
        feeDisplayKVs.add(new GetCardOrCreateAdapter.feeKV("topup fee","0.006%"));
        wallets.add(new GetCardOrCreateAdapter.Wallet(
                GetCardOrCreateAdapter.WalletType.TRON,
                "TYtT9J8eYyY1fXZ7vUqJ3sKm5nBw2cV4dX",
                "100",feeDisplayKVs));

        // 测试1-用户卡正常-返回正常的卡片数据
        if (headers.get("x-test").equals("1")) {
            
            return adapter.responseCardData(
                    new GetCardOrCreateAdapter.CardData(
                            "300 400 500 600",
                            GetCardOrCreateAdapter.CardDataStatus.Activated,
                            false,
                            wallets))
                    .toJSON();
        }
        //测试2-用户卡不可用
        if (headers.get("x-test").equals("2")){
            return adapter.responseCardInvalidated().toJSON();
        }
        //测试3-用户卡KYC需要补充-返回tips提示 
        if (headers.get("x-test").equals("3")){
            // 这个kyc提示是提示前端用户哪些KYC字段需要修改,通常来自于卡商返回的提示信息
            return adapter.responseKCYNotCompleted("first name is required!").toJSON();
        }
        //测试4-用户卡KYC审核中-返回tips提示
        if (headers.get("x-test").equals("4")){
            return adapter.responseKycPending().toJSON();
        }
        //测试5-用户卡不存在,直接开卡成功
        if (headers.get("x-test").equals("5")){
            // 这里要进行开卡动作,开卡失败可以return 其他返回信息
            return adapter.responseCardData(
                    new GetCardOrCreateAdapter.CardData(
                            "300 400 500 600",
                            GetCardOrCreateAdapter.CardDataStatus.Activated,
                            false,
                            wallets))
                    .toJSON();
        }
        //测试6-用户卡不存在,开卡失败显示原因
        if (headers.get("x-test").equals("6")){
            return adapter.responseIssueCardFailed("card create failed!").toJSON();
        }
        //测试7-用户卡不存在,因为KYC资料不齐全而失败
        if (headers.get("x-test").equals("7")){
            // 开卡动作
            // 这个kyc提示是提示前端用户哪些KYC字段需要修改,通常来自于卡商返回的提示信息
            return adapter.responseKCYNotCompleted("first name is required!").toJSON();
        }
        //测试8-用户卡不存在,开卡数量超出了限制
        if (headers.get("x-test").equals("8")){
            return adapter.responseCardCountLimit().toJSON();
        }
        //测试9-未考虑到的错误
        if (headers.get("x-test").equals("9")){
            return adapter.responseOtherError("unknown_error", "unknown error!","more error detail!").toJSON();
        }
        return MsgResult.errMsg("no_action", "not return any result!"  ).toJSON();
      
    }
}

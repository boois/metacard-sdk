package top.metacard.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetCardOrCreateAdapter {
    public enum CardDataStatus {
        Activated,
        NoActivated,
        Unknown,
    }

    public static class CardData {
        public String cardNumber = "";
        public CardDataStatus status = CardDataStatus.Activated;
        public boolean isFrozen = false; // 是否冻结或锁定
        public List<Wallet> wallets;

        public CardData(String cardNumber, CardDataStatus status, boolean isFrozen,List<Wallet> wallets) {
            this.cardNumber = cardNumber;
            this.status = status;
            this.isFrozen = isFrozen;
            this.wallets = wallets;
        }
    }

    public enum WalletType {
        ETH,
        TRON,
        BRC,
        BTC,
    }
    public static class feeKV {
        public String title = "";
        public String content = "";
        public feeKV(String title, String content){
            this.title = title;
            this.content = content;
        }
    }

    public static class Wallet {

        public WalletType type = WalletType.ETH;
        public String address = "";
        public String balance = "";
        public List<feeKV> fee = new ArrayList<feeKV>();

        public Wallet(WalletType type, String address, String balance,List<feeKV> feeDisplayKVs) {
            this.type = type;
            this.address = address;
            this.balance = balance;
            if(feeDisplayKVs != null && !feeDisplayKVs.isEmpty()){
                this.fee = feeDisplayKVs;
            }
        }
        public Wallet(WalletType type, String address, String balance) {
            this.type = type;
            this.address = address;
            this.balance = balance;
        }
    }

    public String userId = ""; // 用户唯一标识
    public String cardId = ""; // 用户唯一标识
    public String bin = ""; // 卡bin
    public Map<String, Object> meta;

    Map<String, Object> headers;
    Map<String, Object> bodyJson;

    public GetCardOrCreateAdapter(Map<String, Object> headers, Map<String, Object> bodyJson) {
        this.headers = headers;
        this.bodyJson = bodyJson;
    }

    public MsgResult init() {
        // 这里会自动处理签名和通信相关的内容
        /*
         1.  读取密钥已经验证签名
          SDKConfig.AppId
		  SDKConfig.SecretToken
         2.  验证参数
         */
        if (!bodyJson.containsKey("user_id")) return MsgResult.errMsg("err_user_id_empty", "user id is empty!");
        userId = bodyJson.get("user_id").toString();
        if (userId.isEmpty()) return MsgResult.errMsg("err_user_id_empty", "user id is empty!");

        if (!bodyJson.containsKey("card_id")) return MsgResult.errMsg("err_card_id_empty", "card id is empty!");
        cardId = bodyJson.get("card_id").toString();
        if (cardId.isEmpty()) return MsgResult.errMsg("err_card_id_empty", "card id is empty!");

        if (!bodyJson.containsKey("bin")) return MsgResult.errMsg("err_card_bin_empty", "card bin is empty!");
        bin = bodyJson.get("bin").toString();
        if (bin.isEmpty()) return MsgResult.errMsg("err_card_bin_empty", "card bin is empty!");

        // 如果读取正确就返回ok
        return new MsgResult("ok", "success", null);
    }

    public MsgResult responseCardCountLimit(int count) {
        return MsgResult.ok();
    }

    public MsgResult responseCardData(CardData cardData) {
        Map<String, Object> result = new HashMap<>();
        result.put("card_number", cardData.cardNumber);
        switch (cardData.status) {
            case Activated -> result.put("status", "activated");
            case NoActivated -> result.put("status", "no_activated");
            case Unknown -> result.put("status", "unknown");
        }
        if (cardData.wallets == null)
            return MsgResult.errMsg("card_wallets_undefined", "card wallets is undefined!");
        if (cardData.wallets.isEmpty())
            return MsgResult.errMsg("card_wallets_empty", "card wallets is empty!");
        for (GetCardOrCreateAdapter.Wallet wallet : cardData.wallets) {
            if (wallet.balance == null || !wallet.balance.matches("^-?\\d+(\\.\\d+)?$")) {
                return MsgResult.errMsg("invalid_wallet_balance", "wallet balance must be a decimal!"+wallet.balance);
            }
            if (wallet.address == null) {
                return MsgResult.errMsg("invalid_wallet_address", "wallet address cannot be null!");
            }
            switch (wallet.type) {
                case ETH, BRC -> {
                    if (!wallet.address.matches("^0x[a-fA-F0-9]{40}$")) {
                        return MsgResult.errMsg("invalid_wallet_address", "ETH/BSC address must start with 0x and be 40 hex chars!");
                    }
                }
                case BTC -> {
                    if (!wallet.address.matches("^(1|3|bc1)[a-zA-Z0-9]{25,34}$")) {
                        return MsgResult.errMsg("invalid_wallet_address", "BTC address must start with 1/3/bc1 and be 26-35 chars!");
                    }
                }
                case TRON -> {
                    if (!wallet.address.matches("^T[a-zA-Z0-9]{33}$")) {
                        return MsgResult.errMsg("invalid_wallet_address", "TRON address must start with T and be 34 chars!");
                    }
                }
                default -> {
                    return MsgResult.errMsg("invalid_wallet_type", "Unsupported wallet type!");
                }
            }
        }
        result.put("is_freezen", cardData.isFrozen);
        result.put("wallets", cardData.wallets);
        return MsgResult.ok(result);
    }

    public MsgResult responseCardInvalidated() {
        return MsgResult.errMsg("card_invalidated", "card is invalidated!");
    }

    public MsgResult responseKycPending() {
        return MsgResult.errMsg("card_kyc_pending", "card is pending!");
    }

    public MsgResult responseKCYNotCompleted(String tips) {
        return MsgResult.errMsg("kyc_not_completed", tips);
    }
    public MsgResult responseCardCountLimit() {
        return MsgResult.errMsg("card_count_limit", "card count limit!");
    }
    public MsgResult responseIssueCardFailed(String tips) {
        return MsgResult.errMsg("issue_card_failed", tips);
    }

    public MsgResult responseOtherError(String msg, String info) {
        return MsgResult.errMsg(msg, info);
    }

    // 先从Meta中取,然后再从KycValue中取
    public MsgResult getValue(String key) {
        return MsgResult.ok(null);
    }

    public MsgResult getMetaValue(String key) {
        return MsgResult.ok(null);
    }

    public MsgResult getKycValue(String key) {
        return MsgResult.ok(null);
    }
}

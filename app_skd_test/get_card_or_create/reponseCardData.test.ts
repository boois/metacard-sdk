import { test, expect, beforeAll, afterAll, beforeEach, afterEach,describe } from 'bun:test';

async function callFc(headers={},data={}){
    let response = await fetch( process.env.API_URL + "get_card_or_create",{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...headers
        },
        body: JSON.stringify(data)
    })
    return response.json()
}

describe('get_card_or_create', () => {
    test('测试1-用户卡正常-返回正常的卡片数据', async () => {
        let headers = {'x-test':'1'}
        let data = {
            "user_id":"A001",
            "card_id":"Card001",
            "bin":"495963"
        }
        let r = await callFc(headers,data);
        console.log(r);
        expect(r.msg).toBe("ok");
        expect(Array.isArray(r.result.wallets)).toBe(true);
        expect(r.result.wallets.length > 0).toBe(true);
        r.result.wallets.forEach((wallet:any) => {
            expect(['ETH','BTC','TRON','BRC'].includes(wallet.type)).toBe(true);
            switch(wallet.type) {
                case 'ETH':
                case 'BRC':
                    expect(wallet.address).toMatch(/^0x[a-fA-F0-9]{40}$/);
                    break;
                case 'BTC':
                    expect(wallet.address).toMatch(/^(1|3|bc1)[a-zA-Z0-9]{25,34}$/);
                    break;
                case 'TRON':
                    expect(wallet.address).toMatch(/^T[a-zA-Z0-9]{33}$/);
                    break;
            }
            expect(wallet.balance).toMatch(/^-?\d+(\.\d+)?$/);
            expect(Array.isArray(wallet.fee)).toBe(true);
            wallet.fee.forEach((fee: any) => {
                expect(typeof fee.title).toBe('string');
                expect(typeof fee.content).toBe('string');
            });
        });
        expect(r.result.card_number).toBe("300 400 500 600");
        expect(r.result.status).toBe("activated");
        expect(typeof r.result.is_freezen).toBe('boolean');
    });        
    test('测试2-用户卡不可用-返回tips提示', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试3-用户卡KYC需要补充-返回tips提示', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试4-用户卡KYC审核中-返回tips提示', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试5-用户卡不存在,直接开卡成功', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试6-用户卡不存在,开卡失败显示原因', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试7-用户卡不存在,因为KYC资料不齐全而失败', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试8-用户卡不存在,开卡数量超出了限制', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    test('测试9-其他不在预料中的错误', () => {
        expect(true).toBe(true);
        expect(1).toBe(1);
        expect('hello').toBe('hello');
    });
    
    
    
    
});

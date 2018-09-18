package com.example.mtk14060.auctionclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
{
    EditText etName, etPass;
    Button bnLogin, bnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etName = findViewById(R.id.userEditText);
        etPass = findViewById(R.id.pwdEditText);
        bnLogin = findViewById(R.id.bnLogin);
        bnCancel = findViewById(R.id.bnCancel);
        bnCancel.setOnClickListener(new HomeListener(this));
        bnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    if (loginPro())
                    {
                        //启动Main Activity
                        Intent intent = new Intent(LoginActivity.this, AuctionClientActivity.class);
                        startActivity(intent);
                        //结束该Activity
                        finish();
                    }
                    else {
                        DialogUtil.showDialog(LoginActivity.this, R.string.user_pwd_fail_prompt, false);
                    }
                }
            }
        });
    }

    //定义处理用户登录请求的方法
    private boolean loginPro() {
        String userName = etName.getText().toString();
        String pwd = etPass.getText().toString();
        JSONObject jsonObject;
        try
        {
            jsonObject = query(userName, pwd);
            if (jsonObject.getInt("userId") > 0)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(this, R.string.server_response_error, false);
            e.printStackTrace();
        }
        return false;
    }

    //定义发送HTTP请求并将服务器响应封装成JSONObject的方法
    private JSONObject query(String userName, String pwd) throws Exception {
        //使用Map封装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("user", userName);
        map.put("pass", pwd);
        String url = HttpUtil.BASE_URL + "login.jsp";
        return new JSONObject(HttpUtil.postRequest(url, map));
    }

    //校验用户名密码
    private boolean validate() {
        String userName = etName.getText().toString();
        if (userName.equals(""))
        {
            DialogUtil.showDialog(this, R.string.user_name_empty, false);
            return false;
        }
        String pwd = etPass.getText().toString();
        if (pwd.equals(""))
        {
            DialogUtil.showDialog(this, R.string.pwd_empty, false);
            return false;
        }
        return true;
    }
}

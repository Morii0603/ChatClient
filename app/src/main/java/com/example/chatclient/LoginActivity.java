package com.example.chatclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class LoginActivity extends AppCompatActivity {

    private EditText ipAddressEditText, portEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ipAddressEditText = findViewById(R.id.ipAddressEditText);
        portEditText = findViewById(R.id.portEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取输入的IP地址和端口
                String ipAddress = ipAddressEditText.getText().toString();
                String port = portEditText.getText().toString();
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                intent.putExtra("ipAddress", ipAddress);
                intent.putExtra("port",port);
                startActivity(intent);


            }
        });
    }
}
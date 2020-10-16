package com.example.outstagram.adapters;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.outstagram.R;
import com.example.outstagram.models.Message;
import com.example.outstagram.models.UserDetails;
import com.example.outstagram.util.UserClient;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MessageRVAdapter extends RecyclerView.Adapter<MessageRVAdapter.MessageChatViewHolder> {
    private static final String TAG = "MessageRVAdapter";
    private List<Message> messagesList = new ArrayList<>();
    private SecretKeySpec secretKeySpec;
    private Cipher decipher;
    private UserDetails currentClient;
    private boolean showMessages = true;

    public MessageRVAdapter(List<Message> messagesList, SecretKeySpec secretKeySpec) {
        this.messagesList = messagesList;
        this.secretKeySpec = secretKeySpec;

        try {
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        UserClient client = UserClient.getInstance();
        currentClient = client.getCurrentUser();
    }



    @NonNull
    @Override
    public MessageChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messages, parent, false);
        MessageChatViewHolder holder = new MessageChatViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageChatViewHolder holder, int position) {

        Message message = messagesList.get(position);
        holder.username.setText(message.getUserName());
        String messageBody = message.getMessage();
        String mess = null;
        if(showMessages){
            try {
                mess = decryptCipher(messageBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else{
            mess = messageBody;
        }

        holder.message_body.setText(mess);

        if(message.getUserName().equals(currentClient.getUserName())){
            Log.d(TAG, "onBindViewHolder: End..");
            /*final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder
                    .linearLayoutContainer.getLayoutParams();
            params.gravity = GravityCompat.END; // or GravityCompat.END
            holder.linearLayoutContainer.setLayoutParams(params);*/
            holder.linearLayoutContainer.setGravity(GravityCompat.END);

            /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.cardView
                    .getLayoutParams();
            params.gravity = GravityCompat.END;
            holder.cardView.setLayoutParams(params);*/


            // holder.linearLayoutContainer.setGravity(Gravity.END);
        }else {
            Log.d(TAG, "onBindViewHolder: Start..");
            //holder.linearLayoutContainer.setGravity(Gravity.START);
            holder.linearLayoutContainer.setGravity(GravityCompat.START);

        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MessageChatViewHolder extends RecyclerView.ViewHolder{

        private TextView username, message_body;
        private LinearLayout linearLayoutContainer;
        private CardView cardView;
        public MessageChatViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.message_username_item);
            message_body = itemView.findViewById(R.id.message_body_item);
            linearLayoutContainer = itemView.findViewById(R.id.message_item_container);
            cardView = itemView.findViewById(R.id.card_message_item);
        }
    }

    private String decryptCipher(String cipherMessage) throws UnsupportedEncodingException {

        Log.d(TAG, "decryptCipher: Called...");
        byte[] encryptedByte2 = cipherMessage.getBytes("ISO-8859-1");


        String decryptedMessage = null;

        byte[] decryption;

        Log.d(TAG, "decryptCipher: Trying Decryption...");

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(encryptedByte2);
            decryptedMessage = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "decryptCipher: Decryption completed... Showing result");



        Log.d(TAG, "decryptCipher: DONE");
        return decryptedMessage;
    }
}

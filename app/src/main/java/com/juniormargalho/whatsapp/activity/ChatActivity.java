package com.juniormargalho.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.juniormargalho.whatsapp.R;
import com.juniormargalho.whatsapp.adapter.MensagensAdapter;
import com.juniormargalho.whatsapp.config.ConfiguracaoFirebase;
import com.juniormargalho.whatsapp.helper.Base64Custom;
import com.juniormargalho.whatsapp.helper.UsuarioFirebase;
import com.juniormargalho.whatsapp.model.Mensagem;
import com.juniormargalho.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //configuração toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurações iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        //recuperar dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        //recuperar dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
            textViewNome.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();

            if(foto != null){
                Uri url = Uri.parse(usuarioDestinatario.getFoto());
                Glide.with(ChatActivity.this).load(url).into(circleImageViewFoto);
            }else {
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }
            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
        }

        //configuração adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //configuração recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);
    }

    public void enviarMensagem(View view){
        String textoMensagem = editMensagem.getText().toString();

        if(!textoMensagem.isEmpty()){
            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem(textoMensagem);

            //Salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

        }else {
            Toast.makeText(ChatActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();
        }

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");
        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(msg); //push() cria um identficador unico no firebase
        editMensagem.setText("");
    }

}

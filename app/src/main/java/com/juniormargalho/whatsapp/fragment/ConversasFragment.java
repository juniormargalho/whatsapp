package com.juniormargalho.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.juniormargalho.whatsapp.R;
import com.juniormargalho.whatsapp.activity.ChatActivity;
import com.juniormargalho.whatsapp.adapter.ConversasAdapter;
import com.juniormargalho.whatsapp.config.ConfiguracaoFirebase;
import com.juniormargalho.whatsapp.helper.RecyclerItemClickListener;
import com.juniormargalho.whatsapp.helper.UsuarioFirebase;
import com.juniormargalho.whatsapp.model.Conversa;
import com.juniormargalho.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ConversasFragment extends Fragment {
    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;

    public ConversasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas);

        //confiigurar adapter
        adapter = new ConversasAdapter(listaConversas, getActivity());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //configurar evento clique
        recyclerViewConversas.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerViewConversas,
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Conversa> listaConversasAtualizada = adapter.getConversas();
                Conversa conversaSelecionada = listaConversasAtualizada.get(position);

                if(conversaSelecionada.getIsGroup().equals("true")){
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatGrupo", conversaSelecionada.getGrupo());
                    startActivity(i);
                }else {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato", conversaSelecionada.getUsuarioExibicao());
                    startActivity(i);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //configura conversasRef
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef = database.child("conversas").child(identificadorUsuario);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void pesquisarConversas(String texto){
        List<Conversa> listaConversasBusca = new ArrayList<>();

        for(Conversa conversa : listaConversas){

            if(conversa.getUsuarioExibicao() != null){
                String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                String ultimaMensageem = conversa.getUltimaMensagem().toLowerCase();

                if(nome.contains(texto) || ultimaMensageem.contains(texto)){
                    listaConversasBusca.add(conversa);
                }
            }else {
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String ultimaMensageem = conversa.getUltimaMensagem().toLowerCase();

                if(nome.contains(texto) || ultimaMensageem.contains(texto)){
                    listaConversasBusca.add(conversa);
                }
            }
        }
        adapter = new ConversasAdapter(listaConversasBusca, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarConversas(){
        adapter = new ConversasAdapter(listaConversas, getActivity());
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recuperarConversas(){
        listaConversas.clear();
        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //recuperar conversas
                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                listaConversas.add(conversa);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

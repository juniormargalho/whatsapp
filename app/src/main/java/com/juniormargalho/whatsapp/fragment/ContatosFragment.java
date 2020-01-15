package com.juniormargalho.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.juniormargalho.whatsapp.R;
import com.juniormargalho.whatsapp.activity.ChatActivity;
import com.juniormargalho.whatsapp.activity.GrupoActivity;
import com.juniormargalho.whatsapp.adapter.ContatosAdapter;
import com.juniormargalho.whatsapp.adapter.ConversasAdapter;
import com.juniormargalho.whatsapp.config.ConfiguracaoFirebase;
import com.juniormargalho.whatsapp.helper.RecyclerItemClickListener;
import com.juniormargalho.whatsapp.helper.UsuarioFirebase;
import com.juniormargalho.whatsapp.model.Conversa;
import com.juniormargalho.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;

    public ContatosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //configgurações iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //configurar adapter
        adapter = new ContatosAdapter(listaContatos, getActivity());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);

        //configurar evento de clique no recyclerview
        recyclerViewListaContatos.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                List<Usuario> listaUsuariosAtualizada = adapter.getContatos();

                Usuario usuarioSelecionado = listaUsuariosAtualizada.get(position);
                boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                if(cabecalho){
                    Intent i = new Intent(getActivity(), GrupoActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("chatContato", usuarioSelecionado);
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
        adicionarMenuNovoGrupo();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){
        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                limparListaContatos();
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    String emailUsuarioAtual = usuarioAtual.getEmail();

                    if(!emailUsuarioAtual.equals(usuario.getEmail())){
                        listaContatos.add(usuario);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void limparListaContatos(){
        listaContatos.clear();
        adicionarMenuNovoGrupo();
    }

    public void adicionarMenuNovoGrupo(){
        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome(getString(R.string.novo_grupo));
        itemGrupo.setEmail("");
        listaContatos.add(itemGrupo);
    }

    public void pesquisarContatos(String texto){
        List<Usuario> listaContatosBusca = new ArrayList<>();

        for(Usuario usuario : listaContatos){
            String nome = usuario.getNome().toLowerCase();
            if(nome.contains(texto)){
                listaContatosBusca.add(usuario);
            }
        }
        adapter = new ContatosAdapter(listaContatosBusca, getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarContatos(){
        adapter = new ContatosAdapter(listaContatos, getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

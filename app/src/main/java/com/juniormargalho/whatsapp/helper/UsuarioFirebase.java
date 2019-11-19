package com.juniormargalho.whatsapp.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.juniormargalho.whatsapp.config.ConfiguracaoFirebase;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }

}

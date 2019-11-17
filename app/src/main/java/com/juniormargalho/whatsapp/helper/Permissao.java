package com.juniormargalho.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static Boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){
        if(Build.VERSION.SDK_INT >= 23){ //Build.VERSION.SDK_INT recupera a versao que esta sendo utilizada do android
            List<String> listaPermissoes = new ArrayList<>();

            //Percorre as permissoes passadas veriificando  uma a uma se ja tem a permissao liberada
            for(String permissao : permissoes){
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED; //PackageManager.PERMISSION_GRANTED guarda as permissoes ja concedidas, salvas pelo android
                if(!temPermissao) listaPermissoes.add(permissao); //adiciona na lista as permissoes que ainda nao foram concedidas
            }
            if(listaPermissoes.isEmpty()) return true;

            //Converte a lista de strings em array de strings
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }
        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.crescer.tcc.controller;

import br.com.crescer.tcc.Models.PartidaModel;
import br.com.crescer.tcc.entity.Grupo;
import br.com.crescer.tcc.entity.Partida;
import br.com.crescer.tcc.entity.Usuario_Grupo;
import br.com.crescer.tcc.entity.Usuario_Partida;
import br.com.crescer.tcc.service.GrupoService;
import br.com.crescer.tcc.service.PartidaService;
import br.com.crescer.tcc.service.Usuario_GrupoService;
import br.com.crescer.tcc.service.Usuario_PartidaService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author luanp
 */
@RestController
@RequestMapping("/partida")
@RequiredArgsConstructor
public class PartidaController {
    private final PartidaService partidaService;
    private final GrupoService grupoService;
    private final Usuario_GrupoService usuario_grupoService;
    private final Usuario_PartidaService usuario_partidaService;
    
    @GetMapping("/{id}")
    public Partida getPartidaById(@PathVariable Long id) {
	return partidaService.loadById(id);
    }
    
    @GetMapping("/lista")
    public List<Partida> list() {
	return partidaService.lista();
    }
    
    @PostMapping("/nova-partida")
    public ResponseEntity<Partida> save(@RequestBody @Valid PartidaModel partidaModel) {
        Grupo grupo = grupoService.loadById(partidaModel.id_grupo);
        
        if(partidaModel.hora_final == null){partidaModel.hora_final = grupo.getHora_final();}
        if(partidaModel.hora_inicio == null){partidaModel.hora_inicio = grupo.getHora_inicio();}
        if(partidaModel.horas_confirmacao == null){partidaModel.horas_confirmacao = grupo.getHoras_confirmacao();}
        if(partidaModel.tempo_avaliacao == null){partidaModel.tempo_avaliacao = grupo.getTempo_avaliacao();}
        if(partidaModel.dia_semana == 0){partidaModel.dia_semana = grupo.getDia_semana();}
        if(partidaModel.dias_confirmacao == 0){partidaModel.dias_confirmacao = grupo.getDias_confirmacao();}
        if(partidaModel.latitude == 0){partidaModel.latitude = grupo.getLatitude();}
        if(partidaModel.longitude == 0){partidaModel.longitude = grupo.getLongitude();}
        if(partidaModel.time_max == 0){partidaModel.time_max = grupo.getTime_max();}
        if(partidaModel.time_min == 0){partidaModel.time_min = grupo.getTime_min();}
        
        Partida partida = new Partida(partidaModel.time_max, partidaModel.time_min, partidaModel.latitude,
        partidaModel.longitude, partidaModel.dia_semana, partidaModel.hora_inicio, partidaModel.hora_final,
        partidaModel.dias_confirmacao, partidaModel.horas_confirmacao, partidaModel.tempo_avaliacao, grupo);
        partidaService.save(partida);
        
        //Carregar todos Usuario_Grupo onde Grupo = grupo para então gazer um for e criar um Usuario_Partida para cada Usuario_Grupo carregado
        List<Usuario_Grupo> listaUsuario_Grupo = usuario_grupoService.findByGrupo(grupo);
        for(Usuario_Grupo usuario_grupo : listaUsuario_Grupo){
            Usuario_Partida usuario_partida = new Usuario_Partida(partida, usuario_grupo);
            usuario_partidaService.save(usuario_partida);
        }
        return ResponseEntity.ok().body(partida);
    }
    
    @PutMapping("/aceitar-partida")
    public ResponseEntity update(@RequestBody @Valid Long id) {
        Usuario_Partida usuario_partida = usuario_partidaService.loadById(id);
        Partida partida = usuario_partida.getPartida();
        if(partida.getTime_atual() < partida.getTime_max()){
            partida.setTime_atual(partida.getTime_atual() + 1);
            partidaService.save(partida);
            return ResponseEntity.ok(usuario_partidaService.update(usuario_partida));
        }else{
            return ResponseEntity.ok("Partida Lotada");
        }
    }
}
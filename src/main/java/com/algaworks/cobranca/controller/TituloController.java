package com.algaworks.cobranca.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.cobranca.model.StatusTitulo;
import com.algaworks.cobranca.model.Titulo;
import com.algaworks.cobranca.repository.Titulos;
import com.algaworks.cobranca.repository.filter.TituloFilter;
import com.algaworks.cobranca.service.CadastroTituloService;

@Controller
@RequestMapping("/titulos")
public class TituloController {
	
	private static final String CADASTRO_VIEW = "CadastroTitulo";
	
	@Autowired
	private Titulos titulos;
	
	@Autowired
	private CadastroTituloService service;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Titulo titulo) {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject("todosStatusTitulo", StatusTitulo.values());
		return mv;
	}
	
	/*Antes de acrescentar o redirect estava assim:
	 * @PostMapping("/salvar")
	public ModelAndView salvar (@Validated Titulo titulo, Errors erross) {
		ModelAndView model = new ModelAndView("CadastroTitulo");
		
		if(erross.hasErrors()) {
			return model;
		}
		titulos.save(titulo);
		
		model.addObject("mensagem", "Título salvo com sucesso!");
		return model ;
		
	}*/
	@PostMapping("/salvar")
	public String salvar (@Validated Titulo titulo, Errors erross, RedirectAttributes attr) {
		
		if(erross.hasErrors()) {
			return CADASTRO_VIEW;
		}
		try {
			if(titulo.getCodigo()!=null) {
				service.salvar(titulo);
				attr.addFlashAttribute("mensagem", "Título editado com sucesso!");
			}else {
				service.salvar(titulo);
				attr.addFlashAttribute("mensagem", "Título salvo com sucesso!");
			}
			
			return "redirect:/titulos/novo" ;
		}catch(IllegalArgumentException e) {
			erross.rejectValue("dataVencimento", null, e.getMessage());
			return CADASTRO_VIEW;
		}
	}
	
	/*Sem mensagem
	 * 	@PostMapping("/salvar")
	public String salvar (Titulo titulo) {
		
		titulos.save(titulo);
		
		return "CadastroTitulo";
		
	}*/
	/*@RequestMapping
	public String pesquisar() {
		return "PesquisaTitulos";
	}*/
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFilter filtro) {
		List<Titulo> todosTitulos = service.filtrar(filtro);
		ModelAndView mv = new ModelAndView("PesquisaTitulos");
		mv.addObject("titulos", todosTitulos);
		return mv;
	}
	
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Long codigoTitulo) {
		Titulo titulo = titulos.getOne(codigoTitulo);
		
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject(titulo);
		return mv;
	}
	
	@PostMapping(value="{codigo}")
	public String excluir(@PathVariable Long codigo , RedirectAttributes attr) {
		service.excluir(codigo);
		
		attr.addFlashAttribute("mensagem", "Título excluído com sucesso!");
		
		return "redirect:/titulos";
		
	}
	
	@PutMapping(value = "/{codigo}/receber")
	public  @ResponseBody String receber(@PathVariable Long codigo) {
		return service.receber(codigo);
		
	}
	/*
	 * RESOLVI O BOTÃO DE RECEBER COM ESTE CODIGO, SEM USAR AJAX
	@RequestMapping("{codigo}/receber")
	public String receber(@PathVariable("codigo") Long codigo , RedirectAttributes attr) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date hoje = new Date();
		Titulo titulo = titulos.getOne(codigo);
		
		titulo.setStatus(StatusTitulo.RECEBIDO);
		
		titulos.save(titulo);
		
		attr.addFlashAttribute("mensagem", "Título recebido em "+ sdf.format(hoje));
		
		return "redirect:/titulos";
		
	}*/

	
	@ModelAttribute("todosStatusTitulo")
	public List<StatusTitulo> allStatusTitulo(){
		return Arrays.asList(StatusTitulo.values());
	}

}

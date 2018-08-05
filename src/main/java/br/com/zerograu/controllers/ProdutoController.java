package br.com.zerograu.controllers;

import br.com.zerograu.domain.Produto;
import br.com.zerograu.services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Controller
public class ProdutoController {
    private ProdutoService produtoService;

    @Autowired
    public void setProdutoService(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }
    /*
     * @RequestMapping(method = RequestMethod.GET, value = "/produto")
     *
     * @ResponseBody public List<Produto> findAll(@RequestParam(value = "search",
     * required = false) String search) { List<SearchCriteria> params = new
     * ArrayList<SearchCriteria>(); if (search != null) { Pattern pattern =
     * Pattern.compile("(\w+?)(:|<|>)(\w+?),"); Matcher matcher =
     * pattern.matcher(search + ","); while (matcher.find()) { params.add(new
     * SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3))); } }
     * return api.searchUser(params); }
     */

    @RequestMapping(value = "/produto/busca", method = RequestMethod.GET)
    public ResponseEntity<List<Produto>> getProduto(@RequestParam("textoBusca") String textoBusca,
                                                    @RequestParam("modulo") String modulo) {
        String textoBuscaDecoded = null;
        String moduloDecoded = null;
        try {
            textoBuscaDecoded = java.net.URLDecoder.decode(textoBusca, "UTF-8");
            moduloDecoded = java.net.URLDecoder.decode(modulo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(textoBuscaDecoded);
        System.out.println(moduloDecoded);
        List<Produto> produtos = produtoService.retornaBusca(textoBuscaDecoded, moduloDecoded);
        if (produtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);// You many decide to return
            // HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @RequestMapping("/produto")
    public String redirToList() {
        return "redirect:/produto/list";
    }

    @RequestMapping(value = "/produto/list", method = RequestMethod.GET)
    public ResponseEntity<List<Produto>> listAllProdutos() {
        List<Produto> produtos = produtoService.listAll();
        if (produtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);// You many decide to return
            // HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/produto/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Produto> getProduto(@PathVariable("id") Integer id) {
        System.out.println("Fetching Produto with id " + id);
        Produto produto = produtoService.getById(id);
        if (produto == null) {
            System.out.println("Produto com o id " + id + " não encontrado");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(produto, HttpStatus.OK);
    }
    // --------------------- CREATE --------------------------------------------------
    @RequestMapping(value = "/produto/", method = RequestMethod.POST)
    public ResponseEntity<Void> createProduto(@RequestBody Produto produto, UriComponentsBuilder ucBuilder) {
        System.out.println("Criando o Produto " + produto.getIdProduto());

        /*
         * if (produtoService.isProdutoExist(produto)) {
         * System.out.println("A Produto with name " + produto.getNomeProduto() +
         * " already exist"); return new ResponseEntity<Void>(HttpStatus.CONFLICT); }
         */
        produtoService.saveOrUpdate(produto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/produto/{id}").buildAndExpand(produto.getIdProduto()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
    // ------------------- Update a Produto
    @RequestMapping(value = "/produto/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Produto> updateProduto(@PathVariable("id") Integer id, @RequestBody Produto produto) {
        System.out.println("Atualizando o Produto " + id);

        Produto currentProduto = produtoService.getById(id);

        if (currentProduto == null) {
            System.out.println("Produto com o " + id + " não foi encontrado");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentProduto.setIdProduto(produto.getIdProduto());

        produtoService.saveOrUpdate(currentProduto);
        return new ResponseEntity<>(currentProduto, HttpStatus.OK);
    }
    // ------------------- Delete a Produto
    // --------------------------------------------------------
    @RequestMapping(value = "/produto/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Produto> deleteProduto(@PathVariable("id") Integer id) {
        System.out.println("Buscando & Deletando o Produto com o id " + id);

        Produto produto = produtoService.getById(id);
        if (produto == null) {
            System.out.println("Não foi possível deletar. Produto com o id " + id + " não encontrado.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        produtoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
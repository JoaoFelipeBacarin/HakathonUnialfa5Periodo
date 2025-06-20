package br.unialfa.hackathon.service;

import br.unialfa.hackathon.repository.AlunoTurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    @Autowired
    private AlunoTurmaRepository repository;
}

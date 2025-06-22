package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.*;
import br.unialfa.hackathon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataInitService implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            initializeData();
        } catch (Exception e) {
            System.err.println("❌ Erro na inicialização: " + e.getMessage());
            // Não quebra a aplicação, apenas falha na inicialização
        }
    }

    private void initializeData() {
        // Criar usuários padrão
        criarUsuariosPadrao();

        // Criar disciplinas
        criarDisciplinas();

        // Criar alunos de exemplo
        criarAlunos();

        // Criar turmas de exemplo (sem transação)
        // criarTurmas(); // Comentado temporariamente

        System.out.println("🎉 Inicialização concluída com sucesso!");
    }

    private void criarUsuariosPadrao() {
        // Admin
        if (!usuarioRepository.existsByLogin("admin")) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNome("Administrador do Sistema");
            admin.setTipo(TipoUsuario.ADMINISTRADOR);
            admin.setEmail("admin@unialfa.br");
            usuarioRepository.save(admin);
            System.out.println("✅ Usuário admin criado com sucesso!");
        }

        // Professor
        if (!usuarioRepository.existsByLogin("professor")) {
            Usuario professor = new Usuario();
            professor.setLogin("professor");
            professor.setPassword(passwordEncoder.encode("prof123"));
            professor.setNome("João Silva");
            professor.setTipo(TipoUsuario.PROFESSOR);
            professor.setEmail("joao.silva@unialfa.br");
            usuarioRepository.save(professor);
            System.out.println("✅ Usuário professor criado com sucesso!");
        }
    }

    private void criarDisciplinas() {
        if (disciplinaRepository.count() == 0) {
            List<Disciplina> disciplinas = Arrays.asList(
                    new Disciplina(null, "JAVA001", "Frameworks Java", "Desenvolvimento web com Spring Boot", 80, true, null),
                    new Disciplina(null, "MOB001", "Programação Mobile", "Desenvolvimento mobile com Flutter", 60, true, null),
                    new Disciplina(null, "WEB001", "Desenvolvimento Web", "HTML, CSS, JavaScript", 80, true, null),
                    new Disciplina(null, "BD001", "Banco de Dados", "MySQL e NoSQL", 60, true, null),
                    new Disciplina(null, "ENG001", "Engenharia de Software", "Metodologias ágeis", 40, true, null)
            );

            disciplinaRepository.saveAll(disciplinas);
            System.out.println("✅ " + disciplinas.size() + " disciplinas criadas com sucesso!");
        }
    }

    private void criarAlunos() {
        if (alunoRepository.count() == 0) {
            List<Aluno> alunos = Arrays.asList(
                    new Aluno(null, "2024001", "Maria Santos", "maria.santos@aluno.unialfa.br", "(44) 99999-0001", "123.456.789-01", true, null, null),
                    new Aluno(null, "2024002", "Pedro Oliveira", "pedro.oliveira@aluno.unialfa.br", "(44) 99999-0002", "123.456.789-02", true, null, null),
                    new Aluno(null, "2024003", "Ana Costa", "ana.costa@aluno.unialfa.br", "(44) 99999-0003", "123.456.789-03", true, null, null),
                    new Aluno(null, "2024004", "Carlos Lima", "carlos.lima@aluno.unialfa.br", "(44) 99999-0004", "123.456.789-04", true, null, null),
                    new Aluno(null, "2024005", "Lucia Fernandes", "lucia.fernandes@aluno.unialfa.br", "(44) 99999-0005", "123.456.789-05", true, null, null),
                    new Aluno(null, "2024006", "Rafael Souza", "rafael.souza@aluno.unialfa.br", "(44) 99999-0006", "123.456.789-06", true, null, null),
                    new Aluno(null, "2024007", "Juliana Rocha", "juliana.rocha@aluno.unialfa.br", "(44) 99999-0007", "123.456.789-07", true, null, null),
                    new Aluno(null, "2024008", "Gabriel Alves", "gabriel.alves@aluno.unialfa.br", "(44) 99999-0008", "123.456.789-08", true, null, null),
                    new Aluno(null, "2024009", "Camila Nunes", "camila.nunes@aluno.unialfa.br", "(44) 99999-0009", "123.456.789-09", true, null, null),
                    new Aluno(null, "2024010", "Bruno Martins", "bruno.martins@aluno.unialfa.br", "(44) 99999-0010", "123.456.789-10", true, null, null)
            );

            alunoRepository.saveAll(alunos);
            System.out.println("✅ " + alunos.size() + " alunos criados com sucesso!");
        }
    }
}
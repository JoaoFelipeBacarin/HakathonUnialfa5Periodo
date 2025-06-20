// listagem_page.dart
import 'package:flutter/material.dart';
import 'package:hackathonflutter/models/aluno.dart';
import 'package:hackathonflutter/models/prova.dart';
import 'package:hackathonflutter/models/turma.dart';
import 'package:hackathonflutter/services/aluno_service.dart';
import 'package:hackathonflutter/services/avaliacao_service.dart';
import 'package:hackathonflutter/ui/pages/gabarito_page.dart';
import 'package:hackathonflutter/ui/widgets/circulo_espera.dart';
import 'package:hackathonflutter/ui/widgets/msg_alerta.dart';
import 'package:hackathonflutter/extensions/string_extension.dart';

// Enum para gerenciar as diferentes visualizações da página
enum ListagemView { turmas, provas, alunos }

class ListagemPage extends StatefulWidget {
  final bool isViewingGabarito; // Novo parâmetro

  const ListagemPage({super.key, this.isViewingGabarito = false}); // Valor padrão false

  @override
  State<ListagemPage> createState() => _ListagemPageState();
}

class _ListagemPageState extends State<ListagemPage> {
  final AvaliacaoService _avaliacaoService = AvaliacaoService();
  final AlunoService _alunoService = AlunoService();

  ListagemView _currentView = ListagemView.turmas;
  String _appBarTitle = 'Turmas';

  List<Turma> _turmas = [];
  List<Prova> _provas = [];
  List<Aluno> _alunos = [];

  Turma? _selectedTurma;
  Prova? _selectedProva; // Alterado para Prova? para ser nulo inicialmente

  bool _carregando = false;

  @override
  void initState() {
    super.initState();
    _carregarTurmas();
  }

  Future<void> _carregarTurmas() async {
    setState(() {
      _carregando = true;
    });
    try {
      _turmas = await _avaliacaoService.buscarTurmas();
      setState(() {
        _currentView = ListagemView.turmas;
        _appBarTitle = 'Turmas';
      });
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Não foi possível carregar as turmas: $e',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  Future<void> _carregarProvas(int turmaId) async {
    setState(() {
      _carregando = true;
    });
    try {
      _provas = await _avaliacaoService.buscarProvasPorTurma(turmaId);
      setState(() {
        _currentView = ListagemView.provas;
        _appBarTitle = 'Provas de ${_selectedTurma!.nome}';
      });
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Não foi possível carregar as provas: $e',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  Future<void> _carregarAlunos(int turmaId) async {
    setState(() {
      _carregando = true;
    });
    try {
      _alunos = await _alunoService.buscarAlunosPorTurma(turmaId);
      setState(() {
        _currentView = ListagemView.alunos;
        _appBarTitle = 'Alunos de ${_selectedTurma!.nome}';
      });
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Não foi possível carregar os alunos: $e',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  void _onBackPressed() {
    if (_currentView == ListagemView.alunos) {
      setState(() {
        _currentView = ListagemView.provas;
        _appBarTitle = 'Provas de ${_selectedTurma!.nome}';
        _selectedAluno = null; // Limpa o aluno selecionado
      });
    } else if (_currentView == ListagemView.provas) {
      setState(() {
        _currentView = ListagemView.turmas;
        _appBarTitle = 'Turmas';
        _selectedTurma = null; // Limpa a turma selecionada
        _selectedProva = null; // Limpa a prova selecionada
      });
    } else {
      Navigator.pop(context); // Se estiver na tela de turmas, volta para a tela anterior
    }
  }

  Aluno? _selectedAluno; // Adicionado para manter o aluno selecionado

  // Este método é chamado quando uma prova é selecionada
  void _selecionarProva(Prova prova) async {
    setState(() {
      _selectedProva = prova;
    });

    if (widget.isViewingGabarito) {
      // Se o objetivo da ListagemPage é apenas ver o gabarito oficial
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => GabaritoPage.forGabarito(
            prova: prova, // Apenas passa a prova
            aluno: null, // explicitamente nulo para visualização de gabarito
          ),
        ),
      );
    } else {
      // Se o objetivo é lançar/editar gabarito de um aluno, precisa selecionar o aluno
      await _carregarAlunos(prova.turmaId);
    }
  }

  // Este método é chamado quando um aluno é selecionado para avaliação
  void _selecionarAlunoParaAvaliacao(Aluno aluno) async {
    // Garante que uma prova foi selecionada antes de prosseguir
    if (_selectedProva == null) {
      MsgAlerta().show(
        context: context,
        titulo: 'Erro',
        texto: 'Por favor, selecione uma prova antes de prosseguir.',
      );
      return;
    }

    setState(() {
      _selectedAluno = aluno;
    });

    // Navega para a GabaritoPage passando o aluno e a prova
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => GabaritoPage(
          aluno: _selectedAluno!, // AGORA ESTÁ SEGURO USAR '!' AQUI
          prova: _selectedProva!, // E AQUI
        ),
      ),
    );

    // Se a GabaritoPage retornar 'true', significa que o gabarito foi enviado
    if (result == true) {
      // Opcional: Recarregar a lista de alunos/respostas aqui se necessário
      // _carregarAlunos(_selectedTurma!.id); // Exemplo de recarga
    }
  }

  Widget _buildTurmasList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16.0),
      itemCount: _turmas.length,
      itemBuilder: (context, index) {
        final turma = _turmas[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 10.0),
          child: ListTile(
            title: Text(turma.nome),
            subtitle: Text('Ano: ${turma.ano}'),
            trailing: const Icon(Icons.arrow_forward_ios),
            onTap: () {
              setState(() {
                _selectedTurma = turma;
              });
              _carregarProvas(turma.id);
            },
          ),
        );
      },
    );
  }

  Widget _buildProvasList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16.0),
      itemCount: _provas.length,
      itemBuilder: (context, index) {
        final prova = _provas[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 10.0),
          child: ListTile(
            title: Text(prova.nome),
            subtitle: Text('Disciplina: ${prova.disciplina}'),
            trailing: const Icon(Icons.arrow_forward_ios),
            onTap: () => _selecionarProva(prova), // Usa o novo método
          ),
        );
      },
    );
  }

  Widget _buildAlunosList() {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: Text(
            'Selecione o aluno para lançar o gabarito da Prova: ${_selectedProva?.nome ?? 'N/A'}',
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          ),
        ),
        Expanded(
          child: ListView.builder(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            itemCount: _alunos.length,
            itemBuilder: (context, index) {
              final aluno = _alunos[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 10.0),
                child: ListTile(
                  title: Text(aluno.nome),
                  subtitle: Text('Turma: ${aluno.turma}'),
                  trailing: const Icon(Icons.arrow_forward_ios),
                  onTap: () => _selecionarAlunoParaAvaliacao(aluno),
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_appBarTitle),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: _onBackPressed,
        ),
      ),
      body: _carregando
          ? const CirculoEspera()
          : Builder(
        builder: (BuildContext context) {
          if (_currentView == ListagemView.turmas) {
            return _buildTurmasList();
          } else if (_currentView == ListagemView.provas) {
            return _buildProvasList();
          } else if (_currentView == ListagemView.alunos) {
            // Este bloco agora sempre construirá a lista de alunos se a view for ALUNOS
            // A condição de isViewingGabarito foi movida para _selecionarProva
            return _buildAlunosList();
          }
          // Fallback para qualquer estado inesperado
          return const Center(child: Text('Estado de visualização desconhecido.'));
        },
      ),
    );
  }
}
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
// import 'package:hackathonflutter/ui/widgets/barra_titulo.dart'; // Removido ou comentado se não for usar


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
  Turma? _selectedTurma;
  Prova? _selectedProva;
  // Aluno? _selectedAluno; // Não será mais necessário manter o aluno aqui para o fluxo de gabarito

  List<Turma> _turmas = [];
  List<Prova> _provas = [];
  List<Aluno> _alunos = [];
  List<Aluno> _alunosFiltrados = []; // Para a busca de alunos

  bool _carregando = true;
  String _searchQuery = ''; // Para o campo de busca de alunos

  @override
  void initState() {
    super.initState();
    // Sempre começa em turmas, a exibição de alunos é que depende de !isViewingGabarito
    _currentView = ListagemView.turmas;
    _carregarDadosIniciais();
  }

  // Novo getter para o título da AppBar baseado na view atual e no modo
  String get _appBarTitle {
    if (widget.isViewingGabarito) {
      if (_currentView == ListagemView.turmas) {
        return 'Selecionar Turma do Gabarito';
      } else if (_currentView == ListagemView.provas) {
        return 'Selecionar Prova do Gabarito';
      }
      // Não haverá ListagemView.alunos para gabarito, então não precisamos de um título aqui
    } else { // Fluxo padrão de seleção de aluno
      if (_currentView == ListagemView.turmas) {
        return 'Selecionar Turma';
      } else if (_currentView == ListagemView.provas) {
        return 'Selecionar Prova';
      } else if (_currentView == ListagemView.alunos) {
        return 'Selecionar Aluno';
      }
    }
    return 'Listagem'; // Fallback
  }

  // Novo método para o comportamento do botão de voltar da AppBar
  void _onBackPressed() {
    if (_carregando) return; // Não permite voltar se estiver carregando

    if (widget.isViewingGabarito) {
      if (_currentView == ListagemView.provas) {
        setState(() {
          _currentView = ListagemView.turmas;
          _selectedTurma = null;
          _selectedProva = null;
          _provas.clear(); // Limpa as provas ao voltar para turmas
        });
      } else if (_currentView == ListagemView.turmas) {
        Navigator.pop(context); // Volta para a HomePage
      }
    } else { // Fluxo de seleção de aluno
      if (_currentView == ListagemView.alunos) {
        setState(() {
          _currentView = ListagemView.provas;
          // Não limpa _selectedProva aqui, pois ainda precisamos dele para filtrar alunos
        });
      } else if (_currentView == ListagemView.provas) {
        setState(() {
          _currentView = ListagemView.turmas;
          _selectedTurma = null;
          _selectedProva = null;
          _alunos.clear();
          _alunosFiltrados.clear();
          _provas.clear(); // Limpa as provas ao voltar para turmas
        });
      } else if (_currentView == ListagemView.turmas) {
        Navigator.pop(context); // Volta para a HomePage
      }
    }
  }


  Future<void> _carregarDadosIniciais() async {
    setState(() {
      _carregando = true;
    });
    try {
      _turmas = await _avaliacaoService.buscarTurmas();
      setState(() {
        _carregando = false;
      });
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Erro ao carregar turmas: $e',
        );
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  Future<void> _selecionarTurma(Turma turma) async {
    setState(() {
      _selectedTurma = turma;
      _carregando = true;
      _currentView = ListagemView.provas; // Sempre vai para provas
      _provas.clear(); // Limpa provas antigas antes de carregar novas
    });

    try {
      _provas = await _avaliacaoService.buscarProvasPorTurma(turma.id);
      setState(() {
        _carregando = false;
      });
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Erro ao carregar provas: $e',
        );
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  Future<void> _selecionarProva(Prova prova) async {
    _selectedProva = prova; // Armazena a prova selecionada

    if (widget.isViewingGabarito) {
      // Se estamos no modo de visualização de gabarito, navega diretamente para GabaritoPage
      if (mounted) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => GabaritoPage.forGabarito(prova: prova), // Usando o construtor nomeado
          ),
        );
      }
    } else {
      // Se estamos no modo de seleção de aluno, carrega os alunos da turma
      setState(() {
        _carregando = true;
        _currentView = ListagemView.alunos;
      });
      try {
        _alunos = await _alunoService.buscarAlunosPorTurma(_selectedTurma!.id);
        _alunosFiltrados = _alunos; // Inicializa a lista filtrada com todos os alunos
        setState(() {
          _carregando = false;
        });
      } catch (e) {
        if (mounted) {
          MsgAlerta().show(
            context: context,
            titulo: 'Erro',
            texto: 'Erro ao carregar alunos: $e',
          );
          setState(() {
            _carregando = false;
          });
        }
      }
    }
  }

  // Renomeado para maior clareza, este método só é chamado no fluxo de seleção de aluno
  void _selecionarAlunoParaAvaliacao(Aluno aluno) {
    if (_selectedProva == null) {
      MsgAlerta().show(
        context: context,
        titulo: 'Atenção',
        texto: 'Por favor, selecione uma prova antes de selecionar o aluno.',
      );
      return;
    }
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => GabaritoPage(
          aluno: aluno,
          prova: _selectedProva!,
        ),
      ),
    );
  }

  void _filterAlunos(String query) {
    setState(() {
      _searchQuery = query;
      if (query.isEmpty) {
        _alunosFiltrados = _alunos;
      } else {
        _alunosFiltrados = _alunos
            .where((aluno) =>
        aluno.nome.toLowerCase().contains(query.toLowerCase()) ||
            aluno.email.toLowerCase().contains(query.toLowerCase()))
            .toList();
      }
    });
  }

  Widget _buildTurmasList() {
    if (_turmas.isEmpty && !_carregando) {
      return const Center(child: Text('Nenhuma turma encontrada.'));
    }
    return ListView.builder(
      itemCount: _turmas.length,
      itemBuilder: (context, index) {
        final turma = _turmas[index];
        return Card(
          margin: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
          elevation: 4,
          child: ListTile(
            contentPadding: const EdgeInsets.all(16.0),
            title: Text(
              turma.nome,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text('Ano: ${turma.ano}'),
            trailing: const Icon(Icons.arrow_forward_ios),
            onTap: () => _selecionarTurma(turma),
          ),
        );
      },
    );
  }

  Widget _buildProvasList() {
    if (_provas.isEmpty && !_carregando) {
      return const Center(child: Text('Nenhuma prova encontrada para esta turma.'));
    }
    return ListView.builder(
      itemCount: _provas.length,
      itemBuilder: (context, index) {
        final prova = _provas[index];
        return Card(
          margin: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
          elevation: 4,
          child: ListTile(
            contentPadding: const EdgeInsets.all(16.0),
            title: Text(
              prova.nome,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text('Disciplina: ${prova.disciplina}'),
            trailing: const Icon(Icons.arrow_forward_ios),
            onTap: () => _selecionarProva(prova),
          ),
        );
      },
    );
  }

  Widget _buildAlunosList() {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: TextField(
            onChanged: _filterAlunos,
            decoration: InputDecoration(
              labelText: 'Buscar Aluno',
              hintText: 'Digite o nome ou e-mail do aluno',
              prefixIcon: const Icon(Icons.search),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8.0),
              ),
            ),
          ),
        ),
        Expanded(
          child: _alunosFiltrados.isEmpty && !_carregando
              ? const Center(child: Text('Nenhum aluno encontrado para esta prova ou na busca.'))
              : ListView.builder(
            itemCount: _alunosFiltrados.length,
            itemBuilder: (context, index) {
              final aluno = _alunosFiltrados[index];
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                elevation: 4,
                child: ListTile(
                  contentPadding: const EdgeInsets.all(16.0),
                  leading: CircleAvatar(
                    backgroundColor: Theme.of(context).colorScheme.primary,
                    child: Text(
                      aluno.nome.initials,
                      style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  title: Text(
                    aluno.nome,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Turma: ${aluno.turma}'),
                      Text(
                        aluno.email,
                        style: TextStyle(color: Colors.grey[600]),
                      ),
                    ],
                  ),
                  trailing: const Icon(Icons.arrow_forward_ios),
                  onTap: () => _selecionarAlunoParaAvaliacao(aluno), // Chamada do método renomeado
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
          } else if (_currentView == ListagemView.alunos && !widget.isViewingGabarito) {
            // Só exibe a lista de alunos se não estiver no modo de visualização de gabarito
            return _buildAlunosList();
          } else {
            // Este caso deve ser alcançado apenas se _currentView for ListagemView.alunos
            // e isViewingGabarito for true, o que não deveria acontecer, pois não há alunos no fluxo de gabarito.
            // Para segurança, retornamos um texto explicativo.
            return const Center(child: Text('Modo de visualização de gabarito não exibe alunos diretamente.'));
          }
        },
      ),
    );
  }
}
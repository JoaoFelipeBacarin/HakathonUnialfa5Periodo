// lib/screens/result_screen.dart
import 'package:flutter/material.dart';
import 'package:hackathonflutter/services/api_service.dart';
import 'package:hackathonflutter/ui/widgets/msg_alerta.dart';
import 'package:hackathonflutter/services/aluno_service.dart'; // Importar AlunoService
import 'package:hackathonflutter/services/avaliacao_service.dart'; // Importar AvaliacaoService
import 'package:hackathonflutter/models/aluno.dart'; // Importar modelo Aluno
import 'package:hackathonflutter/models/prova.dart'; // Importar modelo Prova
import 'package:hackathonflutter/models/resposta.dart'; // Importar o modelo Resposta

class ResultScreen extends StatefulWidget {
  final Map<String, dynamic> extractedData;

  const ResultScreen({Key? key, required this.extractedData}) : super(key: key);

  @override
  State<ResultScreen> createState() => _ResultScreenState();
}

class _ResultScreenState extends State<ResultScreen> {
  late TextEditingController _alunoIdController;
  late TextEditingController _provaIdController;
  late List<Map<String, String>> _respostas;
  final ApiService _apiService = ApiService();
  final AlunoService _alunoService = AlunoService(); // Instância do AlunoService
  final AvaliacaoService _avaliacaoService = AvaliacaoService(); // Instância do AvaliacaoService

  String _alunoNome = 'Carregando...'; // Estado para o nome do aluno
  String _provaNome = 'Carregando...'; // Estado para o nome da prova
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _alunoIdController = TextEditingController(text: widget.extractedData['alunoId']);
    _provaIdController = TextEditingController(text: widget.extractedData['provaId']);
    _respostas = List<Map<String, String>>.from(widget.extractedData['respostas'] ?? []);

    _loadAdditionalData(); // Chamar o método para carregar os nomes do aluno e da prova
  }

  Future<void> _loadAdditionalData() async {
    setState(() {
      _isLoading = true;
    });

    try {
      final String alunoIdStr = _alunoIdController.text;
      final String provaIdStr = _provaIdController.text;

      if (alunoIdStr.isNotEmpty) {
        final int? alunoId = int.tryParse(alunoIdStr);
        if (alunoId != null) {
          final Aluno? aluno = await _alunoService.buscarAlunoPorId(alunoId);
          if (mounted) {
            setState(() {
              _alunoNome = aluno?.nome ?? 'Aluno não encontrado';
            });
          }
        } else {
          if (mounted) {
            setState(() {
              _alunoNome = 'ID de Aluno inválido';
            });
          }
        }
      } else {
        if (mounted) {
          setState(() {
            _alunoNome = 'ID de Aluno ausente';
          });
        }
      }

      if (provaIdStr.isNotEmpty) {
        final int? provaId = int.tryParse(provaIdStr);
        if (provaId != null) {
          final Prova? prova = await _avaliacaoService.buscarProvaPorId(provaId);
          if (mounted) {
            setState(() {
              _provaNome = prova?.nome ?? 'Prova não encontrada';
            });
          }
        } else {
          if (mounted) {
            setState(() {
              _provaNome = 'ID de Prova inválido';
            });
          }
        }
      } else {
        if (mounted) {
          setState(() {
            _provaNome = 'ID de Prova ausente';
          });
        }
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro ao Carregar Dados',
          texto: 'Ocorreu um erro ao buscar as informações do aluno ou da prova: $e',
        );
      }
      setState(() {
        _alunoNome = 'Erro!';
        _provaNome = 'Erro!';
      });
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  void dispose() {
    _alunoIdController.dispose();
    _provaIdController.dispose();
    super.dispose();
  }

  Future<void> _sendData() async {
    setState(() {
      _isLoading = true;
    });

    final String alunoIdStr = _alunoIdController.text.trim();
    final String provaIdStr = _provaIdController.text.trim();

    if (alunoIdStr.isEmpty || provaIdStr.isEmpty) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'IDs Ausentes',
          texto: 'Por favor, preencha o ID do Aluno e o ID da Prova.',
        );
      }
      setState(() {
        _isLoading = false;
      });
      return;
    }

    final int? alunoId = int.tryParse(alunoIdStr);
    final int? provaId = int.tryParse(provaIdStr);

    if (alunoId == null || provaId == null) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'IDs Inválidos',
          texto: 'Por favor, insira IDs numéricos válidos para Aluno e Prova.',
        );
      }
      setState(() {
        _isLoading = false;
      });
      return;
    }

    if (_respostas.isEmpty) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Respostas Ausentes',
          texto: 'Nenhuma resposta foi reconhecida. Verifique o gabarito.',
        );
      }
      setState(() {
        _isLoading = false;
      });
      return;
    }

    // Convertendo a lista de Map<String, String> para List<Resposta>
    List<Resposta> respostasFormatadas = _respostas.map((r) => Resposta(
      alunoId: alunoId, // Usar o alunoId validado
      questaoNumero: int.parse(r['questao']!),
      alternativaSelecionada: r['resposta']!,
      dataResposta: DateTime.now(),
    )).toList();

    try {
      // Usar AvaliacaoService para enviar as respostas
      await _avaliacaoService.enviarRespostasAluno(alunoId, provaId, respostasFormatadas);

      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Sucesso',
          texto: 'Dados do gabarito enviados com sucesso!',
        ).then((_) {
          Navigator.of(context).pop(); // Voltar para a tela anterior
        });
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro no Envio',
          texto: 'Falha ao enviar os dados do gabarito: $e',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Confirmar Gabarito'),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Aluno: $_alunoNome',
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              'Prova: $_provaNome',
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            // Campos de texto para Aluno ID e Prova ID (ainda editáveis)
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _alunoIdController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'ID do Aluno',
                      border: OutlineInputBorder(),
                    ),
                    onChanged: (_) => _loadAdditionalData(), // Recarrega os dados ao mudar o ID
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: TextField(
                    controller: _provaIdController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'ID da Prova',
                      border: OutlineInputBorder(),
                    ),
                    onChanged: (_) => _loadAdditionalData(), // Recarrega os dados ao mudar o ID
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            const Text(
              'Respostas Reconhecidas:',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Container(
              height: 300, // Altura fixa para a lista de respostas
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey),
                borderRadius: BorderRadius.circular(8),
              ),
              child: ListView.builder(
                itemCount: _respostas.length,
                itemBuilder: (context, index) {
                  final resposta = _respostas[index];
                  return Card(
                    margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: [
                          Text('Questão ${resposta['questao']}:', style: const TextStyle(fontWeight: FontWeight.bold)),
                          const SizedBox(width: 10),
                          Expanded(
                            child: TextField(
                              controller: TextEditingController(text: resposta['resposta']),
                              onChanged: (newValue) {
                                setState(() {
                                  _respostas[index]['resposta'] = newValue.toUpperCase();
                                });
                              },
                              decoration: const InputDecoration(
                                border: OutlineInputBorder(),
                                contentPadding: EdgeInsets.symmetric(horizontal: 10, vertical: 8),
                              ),
                              textAlign: TextAlign.center,
                              maxLength: 1,
                              textCapitalization: TextCapitalization.characters,
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
            const SizedBox(height: 20),
            Center(
              child: _isLoading
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                onPressed: _sendData,
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size(double.infinity, 50),
                ),
                child: const Text('Enviar Dados do Gabarito'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
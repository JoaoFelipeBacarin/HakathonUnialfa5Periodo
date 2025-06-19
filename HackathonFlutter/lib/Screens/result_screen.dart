import 'package:flutter/material.dart';
import 'package:hackathonflutter/services/api_service.dart';


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
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _alunoIdController = TextEditingController(text: widget.extractedData['alunoId']);
    _provaIdController = TextEditingController(text: widget.extractedData['provaId']);
    _respostas = List<Map<String, String>>.from(widget.extractedData['respostas']);
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

    final dataToSend = {
      "alunoId": _alunoIdController.text,
      "provaId": _provaIdController.text,
      "respostas": _respostas,
    };

    try {
      final response = await _apiService.sendGabaritoData(dataToSend);
      print('API Response: $response');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(response['message'])),
      );
      if (response['status'] == 'success') {
        Navigator.pop(context); // Voltar para a tela da câmera se o envio for bem-sucedido
      }
    } catch (e) {
      print('Error sending data: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erro ao enviar dados: $e')),
      );
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Resultados do Gabarito')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('ID do Aluno:', style: Theme.of(context).textTheme.titleMedium),
            TextFormField(
              controller: _alunoIdController,
              decoration: const InputDecoration(border: OutlineInputBorder()),
            ),
            const SizedBox(height: 16.0),
            Text('ID da Prova:', style: Theme.of(context).textTheme.titleMedium),
            TextFormField(
              controller: _provaIdController,
              decoration: const InputDecoration(border: OutlineInputBorder()),
            ),
            const SizedBox(height: 16.0),
            Text('Respostas:', style: Theme.of(context).textTheme.titleMedium),
            Expanded(
              child: ListView.builder(
                itemCount: _respostas.length,
                itemBuilder: (context, index) {
                  final resposta = _respostas[index];
                  return Card(
                    margin: const EdgeInsets.symmetric(vertical: 8.0),
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: [
                          Text('Questão ${resposta['questao']}: '),
                          Expanded(
                            child: TextFormField(
                              initialValue: resposta['resposta'],
                              onChanged: (newValue) {
                                setState(() {
                                  _respostas[index]['resposta'] = newValue.toUpperCase();
                                });
                              },
                              decoration: const InputDecoration(border: OutlineInputBorder()),
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
            Center(
              child: _isLoading
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: _sendData,
                      child: const Text('Enviar Dados'),
                    ),
            ),
          ],
        ),
      ),
    );
  }
}



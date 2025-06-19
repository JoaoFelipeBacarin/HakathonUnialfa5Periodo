class Prova {
  final int id;
  final String nome;
  final String disciplina;
  final int turmaId; // Para associar a prova a uma turma

  Prova({
    required this.id,
    required this.nome,
    required this.disciplina,
    required this.turmaId,
  });

  factory Prova.fromJson(Map<String, dynamic> json) {
    return Prova(
      id: json['id'],
      nome: json['nome'],
      disciplina: json['disciplina'],
      turmaId: json['turmaId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nome': nome,
      'disciplina': disciplina,
      'turmaId': turmaId,
    };
  }
}
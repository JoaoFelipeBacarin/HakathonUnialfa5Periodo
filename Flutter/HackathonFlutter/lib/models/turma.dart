class Turma {
  final int id;
  final String nome;
  final String ano;

  Turma({required this.id, required this.nome, required this.ano});

  factory Turma.fromJson(Map<String, dynamic> json) {
    return Turma(
      id: json['id'],
      nome: json['nome'],
      ano: json['ano'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nome': nome,
      'ano': ano,
    };
  }
}
import { Component } from '@angular/core';
import { IaService , ChatMessage  } from 'app/services/ia.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ia-chat',
  imports: [CommonModule, FormsModule],
  templateUrl: './ia-chat.component.html',
  styleUrl: './ia-chat.component.css',
})
export class IaChatComponent {
  question: string = '';
  messages: ChatMessage[] = [];
  loading: boolean = false;
  errorMsg: string = '';

  constructor(private iaService: IaService) {}

  envoyer(): void {
    const q = this.question.trim();
    if (!q || this.loading) return;

    // Affiche immédiatement la question de l'utilisateur
    this.messages.push({ role: 'user', content: q });
    this.question = '';
    this.loading = true;
    this.errorMsg = '';

    this.iaService.chat(q).subscribe({
      next: (reponse: string) => {
        this.messages.push({ role: 'assistant', content: reponse });
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chat IA :', err);
        this.errorMsg = "Une erreur est survenue lors de la communication avec l'assistant IA.";
        this.loading = false;
      }
    });
  }
   nouvelleConversation(): void {
    this.messages = [];
    this.errorMsg = '';
  }

  onEnter(event: Event): void {
    event.preventDefault();
    this.envoyer();
  }

}

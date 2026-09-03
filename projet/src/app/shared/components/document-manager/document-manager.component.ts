import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import {
  AppDocument,
  DocumentService,
  TypeEntiteDocument,
} from 'app/services/document.service';

interface UploadTask {
  fileName: string;
  progress: number;
  error?: string;
}

@Component({
  selector: 'app-document-manager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './document-manager.component.html',
  styleUrl: './document-manager.component.css',
})
export class DocumentManagerComponent implements OnInit, OnChanges {
  @Input({ required: true }) entityType!: TypeEntiteDocument;
  @Input({ required: true }) entityId!: number;
  @Input() title = 'Documents';

  private static readonly ALLOWED_EXTENSIONS = ['pdf', 'jpg', 'jpeg', 'png', 'docx'];
  private static readonly MAX_SIZE_BYTES = 10 * 1024 * 1024;

  documents: AppDocument[] = [];
  loading = false;
  isDragActive = false;
  uploadTasks: UploadTask[] = [];
  errorMessage = '';

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    this.loadDocuments();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['entityId'] || changes['entityType']) && this.entityId) {
      this.loadDocuments();
    }
  }

  loadDocuments(): void {
    if (!this.entityId || !this.entityType) return;
    this.loading = true;
    this.documentService.getByEntity(this.entityType, this.entityId).subscribe({
      next: (docs) => {
        this.documents = docs;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragActive = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragActive = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragActive = false;
    const files = event.dataTransfer?.files;
    if (files?.length) this.handleFiles(files);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) this.handleFiles(input.files);
    input.value = '';
  }

  private handleFiles(files: FileList): void {
    this.errorMessage = '';
    Array.from(files).forEach((file) => this.uploadFile(file));
  }

  private uploadFile(file: File): void {
    const validationError = this.validateFile(file);
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    const task: UploadTask = { fileName: file.name, progress: 0 };
    this.uploadTasks.push(task);

    this.documentService.upload(this.entityType, this.entityId, file).subscribe({
      next: (update) => {
        task.progress = update.progress;
        if (update.done) {
          this.uploadTasks = this.uploadTasks.filter((t) => t !== task);
          this.loadDocuments();
        }
      },
      error: (err) => {
        task.error = err?.error?.message || "Échec de l'upload.";
        setTimeout(() => {
          this.uploadTasks = this.uploadTasks.filter((t) => t !== task);
        }, 4000);
      },
    });
  }

  private validateFile(file: File): string | null {
    const extension = file.name.split('.').pop()?.toLowerCase() || '';
    if (!DocumentManagerComponent.ALLOWED_EXTENSIONS.includes(extension)) {
      return `Type de fichier non autorisé (.${extension}). Types acceptés : PDF, JPG, PNG, DOCX.`;
    }
    if (file.size > DocumentManagerComponent.MAX_SIZE_BYTES) {
      return 'Le fichier dépasse la taille maximale autorisée (10 Mo).';
    }
    return null;
  }

  download(doc: AppDocument): void {
    if (doc.id) this.documentService.download(doc.id, doc.nomFichier || 'document');
  }

  remove(doc: AppDocument): void {
    if (!doc.id) return;
    if (!confirm(`Supprimer le document "${doc.nomFichier}" ?`)) return;
    this.documentService.delete(doc.id).subscribe({
      next: () => this.loadDocuments(),
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Échec de la suppression.';
      },
    });
  }

  formatSize(bytes?: number): string {
    if (!bytes) return '—';
    if (bytes < 1024) return `${bytes} o`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} Ko`;
    return `${(bytes / (1024 * 1024)).toFixed(2)} Mo`;
  }
}

import { ProviderMessageService } from "../api/MessageServices";
import { ReceiveMsg } from "../../msg/msg";
import {
  ClientProviderListMsg,
  ClientProviderAddTokenMsg,
  ClientProviderDeletedMsg,
} from "../../msg/provider";
import { Provider, CreateProviderInput, CompleteProviderInput } from "./ProviderTypes";
import { validateCreateInput } from "./ProviderUtils";

/** プロバイダー関連のビジネスロジックを管理するドメインサービス */
export class ProviderService {
  private providerListCallbacks: Set<(providers: Provider[]) => void> = new Set();
  private tokenIssuedCallbacks: Set<(token: string) => void> = new Set();
  private idpAddedCallbacks: Set<() => void> = new Set();
  private providerDeletedCallbacks: Set<(id: string) => void> = new Set();

  constructor(private providerMessageService: ProviderMessageService) {}

  listProviders(): void {
    this.providerMessageService.listProviders();
  }

  addProvider(input: CreateProviderInput): void {
    const validation = validateCreateInput(input);
    if (!validation.valid) throw new Error(validation.errors.join(", "));
    this.providerMessageService.addProvider(input.name.trim(), input.issuer.trim(), input.audience.trim());
  }

  completeProviderAdd(input: CompleteProviderInput): void {
    if (!input.token.trim()) throw new Error("Token is required");
    if (!input.code.trim()) throw new Error("Authorization code is required");
    if (!input.redirectUri.trim()) throw new Error("Redirect URI is required");
    this.providerMessageService.completeProviderAdd(input.token.trim(), input.code.trim(), input.redirectUri.trim());
  }

  deleteProvider(id: string): void {
    if (!id.trim()) throw new Error("Provider ID is required");
    this.providerMessageService.deleteProvider(id.trim());
  }

  handleMessage(message: ReceiveMsg): void {
    switch (message.type) {
      case "provider_listed": this.handleProviderListed(message as ClientProviderListMsg); break;
      case "provider_add_token_issued": this.handleTokenIssued(message as ClientProviderAddTokenMsg); break;
      case "provider_idp_added": this.handleIdpAdded(); break;
      case "provider_deleted": this.handleProviderDeleted(message as ClientProviderDeletedMsg); break;
    }
  }

  onProviderList(callback: (providers: Provider[]) => void): () => void {
    this.providerListCallbacks.add(callback);
    return () => this.providerListCallbacks.delete(callback);
  }

  onTokenIssued(callback: (token: string) => void): () => void {
    this.tokenIssuedCallbacks.add(callback);
    return () => this.tokenIssuedCallbacks.delete(callback);
  }

  onIdpAdded(callback: () => void): () => void {
    this.idpAddedCallbacks.add(callback);
    return () => this.idpAddedCallbacks.delete(callback);
  }

  onProviderDeleted(callback: (id: string) => void): () => void {
    this.providerDeletedCallbacks.add(callback);
    return () => this.providerDeletedCallbacks.delete(callback);
  }

  private handleProviderListed(message: ClientProviderListMsg): void {
    const providers: Provider[] = message.providers.map((p) => ({
      id: p.id, name: p.name, issuer: p.issuer, audience: p.audience,
      idps: p.idps.map((idp) => ({ issuer: idp.issuer, subject: idp.subject, audience: idp.audience })),
    }));
    this.providerListCallbacks.forEach((cb) => cb(providers));
  }

  private handleTokenIssued(message: ClientProviderAddTokenMsg): void {
    this.tokenIssuedCallbacks.forEach((cb) => cb(message.token));
  }

  private handleIdpAdded(): void {
    this.idpAddedCallbacks.forEach((cb) => cb());
  }

  private handleProviderDeleted(message: ClientProviderDeletedMsg): void {
    this.providerDeletedCallbacks.forEach((cb) => cb(message.id));
  }
}

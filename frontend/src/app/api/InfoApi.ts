export interface InfoApi {
  isSetup(): Promise<boolean>;

  version(): Promise<string>;
}

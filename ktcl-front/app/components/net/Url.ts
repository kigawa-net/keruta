/**
 * kodel Url API互換のURLユーティリティクラス
 */
export class Url implements UrlBase {
    constructor(private readonly url: URL) {
    }

    static parse(strUrl: string): Url {
        return new Url(new URL(strUrl));
    }

    get path(): string {
        return this.url.pathname;
    }

    setPath(path: string): Url {
        const newUrl = new URL(this.url.toString());
        newUrl.pathname = path;
        return new Url(newUrl);
    }

    plusPath(path: string): Url {
        const base = this.path.replace(/\/$/, "");
        const newPath = path.replace(/^\//, "");
        return this.setPath(`${base}/${newPath}`);
    }

    toStrUrl(): string {
        return this.url.toString();
    }

    toString(): string {
        return this.toStrUrl();
    }

    toJsURL(): URL {
        return this.url;
    }

    /** クエリパラメータを設定 */
    setQueryParam(key: string, value: string): Url {
        const newUrl = new URL(this.url.toString());
        newUrl.searchParams.set(key, value);
        return new Url(newUrl);
    }

    /** 生のURLオブジェクトを取得 */
    get raw(): URL {
        return this.url;
    }
}

export interface UrlBase {
    readonly path: string;

    setPath(path: string): Url;

    plusPath(path: string): Url;

    toStrUrl(): string;
}

import * as kiclDomain from "keruta-kicl-kicl-domain";

export default function Index() {
    const version = kiclDomain.net.kigawa.keruta.kicl.domain.KiclDomain.VERSION;
    return (
        <div>
            <h1>kicl</h1>
            <p>version: {version}</p>
        </div>
    );
}

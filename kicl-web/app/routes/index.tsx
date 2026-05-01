import {KiclDomain} from "keruta-kicl-kicl-domain";

export default function Index() {
    const version = KiclDomain.getInstance().VERSION;
    return (
        <div>
            <h1>kicl</h1>
            <p>version: {version}</p>
        </div>
    );
}

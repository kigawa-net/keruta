
import {Link} from "react-router";
import {ProviderAddForm} from "../components/provider/ProviderAddForm";

// noinspection JSUnusedGlobalSymbols
export default function ProviderAddRoute() {
    return (
        <div className="max-w-2xl mx-auto p-6">
            <div className="flex items-center gap-4 mb-8">
                <Link to="/provider" className="text-gray-500 hover:text-gray-700">← 戻る</Link>
                <h1 className="text-3xl font-bold">プロバイダーを追加</h1>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
                <ProviderAddForm/>
            </div>
        </div>
    )
}

import {Link} from "react-router";
import {ProviderAddForm} from "../components/provider/ProviderAddForm";

// noinspection JSUnusedGlobalSymbols
export default function ProviderAddRoute() {
    return (
        <div className="max-w-2xl mx-auto p-3 md:p-6">
            <div className="flex items-center gap-2 md:gap-4 mb-6 md:mb-8">
                <Link to="/provider" className="text-gray-500 hover:text-gray-700 text-sm md:text-base">← 戻る</Link>
                <h1 className="text-2xl md:text-3xl font-bold">プロバイダーを追加</h1>
            </div>
            <div className="bg-white rounded-lg shadow p-4 md:p-6">
                <ProviderAddForm/>
            </div>
        </div>
    )
}

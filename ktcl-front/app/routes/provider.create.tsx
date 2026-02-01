import {ProviderCreateForm} from "../components/provider/ProviderCreateForm";

export default function ProviderCreateRoute() {
    return (
        <div className="max-w-2xl mx-auto p-6">
            <h1 className="text-3xl font-bold mb-8">プロバイダー追加</h1>
            <ProviderCreateForm/>
        </div>
    )
}

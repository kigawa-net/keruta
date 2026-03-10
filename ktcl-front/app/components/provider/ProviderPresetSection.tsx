import React from "react";
import {Link} from "react-router";
import {PROVIDER_PRESETS} from "./ProviderPresets";

export function ProviderPresetSection() {
    return (
        PROVIDER_PRESETS.length === 0 ? undefined :
        <div className="mb-6 md:mb-8">
            <h2 className="text-lg font-semibold mb-3 text-gray-700">プリセットから追加</h2>
            <div className="flex flex-wrap gap-3">
                {PROVIDER_PRESETS.map((preset,index) => (
                    <Link
                        key={index}
                        to={`/provider/add?issuer=${encodeURIComponent(preset.issuer)}`}
                        className="flex flex-col px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg hover:bg-blue-50 hover:border-blue-300 transition-colors"
                    >
                        <span className="font-medium text-gray-800">{preset.name}</span>
                        <span className="text-xs text-gray-500 mt-1">{preset.description}</span>
                    </Link>
                ))}
            </div>
        </div>
    );
}

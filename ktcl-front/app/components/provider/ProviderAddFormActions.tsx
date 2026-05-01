import React from "react";
import {Link} from "react-router";

interface ProviderAddFormActionsProps {
    isDisabled: boolean;
    buttonLabel: string;
}

export function ProviderAddFormActions({isDisabled, buttonLabel}: ProviderAddFormActionsProps) {
    return (
        <div className="flex flex-col sm:flex-row gap-3 sm:gap-4">
            <button
                type="submit"
                className="w-full sm:w-auto px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
                disabled={isDisabled}
            >
                {buttonLabel}
            </button>
            <Link
                to="/provider"
                className="w-full sm:w-auto px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors text-center"
            >
                キャンセル
            </Link>
        </div>
    );
}

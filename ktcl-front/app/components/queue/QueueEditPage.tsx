import {QueueEditForm} from "./QueueEditForm";

export default function QueueEditPage({queueId}: { queueId: number }) {
    return (
        <div className="h-full p-4 md:p-8">
            <div className="max-w-2xl mx-auto">
                <div className="mb-6 md:mb-8">
                    <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Edit Queue</h1>
                    <p className="mt-2 text-gray-600">キューを編集します</p>
                </div>

                <div className="bg-white rounded-lg shadow p-4 md:p-6">
                    <QueueEditForm queueId={queueId}/>
                </div>
            </div>
        </div>
    );
}

import {QueueCreateForm} from "./QueueCreateForm";

export default function QueueCreatePage() {
  return (
    <div className="h-full p-8">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Create Queue</h1>
          <p className="mt-2 text-gray-600">新しいキューを作成します</p>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <QueueCreateForm />
        </div>
      </div>
    </div>
  );
}

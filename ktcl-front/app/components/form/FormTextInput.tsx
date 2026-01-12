import FormErrMsg from "./FormErrMsg";

export interface TextInputValue {
    value: string,
    error?: string
}

export default function FormTextInput(
    {
        label,
        id,
        placeholder,
        value,
        onChange,
    }: {
        label: string,
        id: string,
        placeholder: string,
        value: TextInputValue,
        onChange: (value: TextInputValue) => void,
    },
) {
    return (
        <div>
            <label htmlFor="taskName" className="block text-sm font-medium text-gray-700 mb-2">
                {label}
            </label>
            <input
                type="text"
                id={id}
                value={value.value}
                onChange={
                    (e) => onChange({value: e.target.value, error: undefined})
                }
                className={
                    "w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2" +
                    " focus:ring-blue-500"
                }
                placeholder={placeholder}
            />
            <FormErrMsg err={value.error}/>
        </div>
    )
}



import React, { useState, useEffect } from "react";
import axios from "axios";

function Calculator() {
  const [display, setDisplay] = useState("");
  const [plugins, setPlugins] = useState([]);
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [pluginValue, setPluginValue] = useState("");
  const [activePlugin, setActivePlugin] = useState(false);
  const [activeNamePlugin, setactiveNamePlugin] = useState(false);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/plugins/plugins")
      .then((response) => setPlugins(response.data))
      .catch((error) =>
        console.error("Erreur lors du chargement des plugins", error)
      );
    axios
      .get("http://localhost:8080/api/files")
      .then((response) => setFiles(response.data))
      .catch((error) =>
        console.error("Erreur lors du chargement des plugins", error)
      );
  }, []);

  const handleButtonClick = (value) => {
    if (activePlugin) {
      setPluginValue((prev) => prev + value);
      setDisplay((prev) => prev + value);
    } else {
      setDisplay((prev) => prev + value);
    }
  };

  const handleClear = () => {
    setDisplay("");
    setActivePlugin(false);
    setactiveNamePlugin("");
    setPluginValue("");
  };

  const handleCalculate = () => {
    try {
      const result = eval(display);
      setDisplay(result.toString());
      setActivePlugin(false);
      setactiveNamePlugin("");
    } catch (error) {
      alert("Expression invalide !");
    }
  };

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handlePluginUpload = () => {
    if (!selectedFile) {
      alert("Veuillez sélectionner un fichier !");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);

    axios
      .post("http://localhost:8080/api/plugins/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((response) => {
        alert("Plugin ajouté avec succès : " + response.data.pluginName);
        setPlugins((prevPlugins) => [...prevPlugins, response.data.pluginName]);
      })
      .catch((error) => {
        console.error("Erreur lors de l'ajout du plugin", error);
        alert("Erreur lors de l'ajout du plugin.");
      });
  };

  const executePlugin = () => {
    if (!pluginValue) {
      alert("Veuillez entrer une valeur pour le plugin !");
      return;
    }
    axios
      .post("http://localhost:8080/api/plugins/execute-plugin", {
        name: activeNamePlugin,
        value: parseFloat(pluginValue),
      })
      .then((response) => {
        setDisplay(response.data);
        setActivePlugin(false);
        setactiveNamePlugin("");
      })
      .catch((error) => {
        console.error("Erreur lors de l'exécution du plugin:", error);
        setDisplay("Erreur lors de l'exécution du plugin");
      });
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white shadow-lg rounded-lg p-6 w-3/4 flex">
        {/* Colonne des plugins */}
        <div className="w-1/2 pr-4 border-r border-gray-300">
          <div className="flex items-center mb-4">
            <input
              type="file"
              accept=".jar,.java,.class"
              id="fileInput"
              onChange={handleFileChange}
              className="hidden"
            />
            <label
              htmlFor="fileInput"
              className="bg-blue-500 text-white rounded p-3 cursor-pointer hover:bg-blue-600 transition-colors w-full text-center"
            >
              Choisir un fichier
            </label>
          </div>

          <button
            onClick={handlePluginUpload}
            className="bg-green-500 text-white rounded p-3 hover:bg-green-600 transition-colors w-full mb-4"
          >
            Ajouter un Plugin
          </button>

          <input
            type="text"
            placeholder="Rechercher un plugin..."
            onChange={(e) => setSearchTerm(e.target.value)}
            className="p-2 border border-gray-300 rounded w-full mb-4"
          />

          <div className="max-h-48 overflow-y-auto">
            {files
              .filter((plugin) =>
                plugin.toLowerCase().includes(searchTerm.toLowerCase())
              )
              .map((plugin) => (
                <button
                  key={plugin}
                  onClick={() => executePlugin(plugin)}
                  className="bg-purple-500 text-white rounded p-3 mb-2 w-full hover:bg-purple-600 transition-colors text-left flex items-center"
                >
                  <span className="mr-2">🔌</span>
                  {plugin}
                </button>
              ))}
          </div>
        </div>

        {/* Colonne de la calculatrice */}
        <div className="w-1/2 pl-4">
          <div className="text-center mb-4">
            <input
              type="text"
              value={display}
              readOnly
              className="p-2 border border-gray-300 rounded w-full mb-4 text-center"
            />
            <div className="grid grid-cols-4 gap-2">
              {[
                "7",
                "8",
                "9",
                "C",
                "4",
                "5",
                "6",
                "/",
                "1",
                "2",
                "3",
                "*",
                "0",
                "+",
                "-",
                "=",
                " )",
              ].map((value) => (
                <button
                  key={value}
                  onClick={() =>
                    value === "C"
                      ? handleClear()
                      : value === "=" && activePlugin == false
                      ? handleCalculate()
                      : value === "=" && activePlugin == true
                      ? executePlugin()
                      : handleButtonClick(value)
                  }
                  className="bg-gray-200 p-3 rounded hover:bg-gray-300"
                >
                  {value}
                </button>
              ))}
            </div>
            <div className="grid grid-cols-4 gap-2">
              {plugins.map((value) => (
                <button
                  key={value}
                  onClick={() => {
                    setActivePlugin(true);
                    setDisplay(value + "( ");
                    setactiveNamePlugin(value);
                  }}
                  className="bg-green-200 mt-3 mb-3 p-3 rounded hover:bg-gray-300"
                >
                  {value}
                </button>
              ))}
            </div>
            <input
              type="text"
              placeholder="Entrez la valeur du plugin..."
              value={pluginValue}
              onChange={(e) => setPluginValue(e.target.value)}
              className="p-2 border border-gray-300 rounded w-full mb-4"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Calculator;

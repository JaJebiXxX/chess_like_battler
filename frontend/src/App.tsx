import React, { useEffect, useState } from "react";

function App() {
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then((response: Response) => response.text())
      .then((data: string) => setMessage(data))
      .catch((err) => console.error("Error:", err));
  }, []);

  return (
    <div style={{ padding: "2rem", fontFamily: "sans-serif" }}>
      <h1>React ↔ Spring Boot</h1>
      <p>{message}</p>
    </div>
  );
}

export default App

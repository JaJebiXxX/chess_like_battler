import { useState, useEffect } from 'react'
import './App.css'

// Define types based on backend structure
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
enum FieldType {
  NORMAL = "NORMAL",
  MANA = "MANA",
  HP_FIGURE = "HP_FIGURE",
  HP_BASE = "HP_BASE"
}

//⠀⠀⠘⡀⠀⠀HOG RIDAAAAAA⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡜⠀⠀⠀
// ⠀⠀⠀⠑⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡔⠁⠀⠀⠀
// ⠀⠀⠀⠀⠈⠢⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⠴⠊⠀⠀⠀⠀⠀
// ⠀⠀⠀⠀⠀⠀⠀⢸⠀⠀⠀⢀⣀⣀⣀⣀⣀⡀⠤⠄⠒⠈⠀⠀⠀⠀⠀⠀⠀⠀
// ⠀⠀⠀⠀⠀⠀⠀⠘⣀⠄⠊⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
// ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠏⠉⢈⠩⢙⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⢋⠠⠀⠀⠨⠐⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⢐⠐⠌⡌⢄⢐⢈⠔⡝⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⣿⣿⣿⣿⡏⠉⡀⠐⡀⢁⠈⠐⠱⠑⡑⠈⢹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⣿⣿⣿⣿⢗⠀⠀⠐⡠⡛⠔⡁⢜⡔⡬⢎⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⣿⣿⣿⡿⠡⠀⠀⠀⠀⠂⠁⠀⠄⢂⠈⠂⢂⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⡿⢟⠩⠐⡀⠀⠀⠀⠐⠐⠁⠓⠒⠒⢀⠁⢐⢝⢟⢿⣿⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⠫⠡⠡⠨⢀⠂⠠⠀⠀⢁⠑⡱⠛⠗⡓⢂⠠⢸⢸⢨⠣⡝⣻⣿⣿⣿⣿⣿⣿
// ⣿⢏⢐⢁⠊⢌⠐⡈⠄⠠⠀⠀⠀⠀⠁⠑⠈⠀⢄⢕⠸⡨⠪⡪⡘⣻⣿⡿⣿⣿⣿
// ⣿⢂⠂⡂⠅⡂⠅⡐⠨⢐⠐⠠⠠⡀⢄⠠⡠⡡⡱⡐⠕⢌⢊⢆⢣⢒⠽⢿⣿⣿⣿
// ⠣⢂⠂⠄⠡⠐⠐⠈⠌⡐⠨⡈⠢⠨⡂⢌⢂⠆⡪⠨⡊⠂⡂⠢⢡⣢⣣⡣⣍⢿⣿
// ⠨⢂⢂⠁⡀⠀⠀⠁⠐⠈⠐⠈⢈⠈⠐⡀⠄⠁⠌⠈⠔⣄⡀⠠⡑⡂⠆⠢⢂⠑⠽
// ⡨⠐⠀⠀⠀⢠⡎⡀⠀⠀⠄⠈⡀⠌⠐⠠⠈⠄⡁⠂⡀⡫⠑⣑⠀⢂⠌⠄⢕⠀⠨
// ⠺⡪⠢⡀⠀⠞⢇⢂⠀⠂⡀⠠⠀⠄⠁⠌⠨⠀⢄⠢⡁⢂⢿⡟⡀⠀⠈⠈⡀⠂⣰
// ⢀⢀⠀⠄⠀⠀⡐⠀⡈⠄⡐⠅⡊⠌⢌⠄⡕⡑⡁⢂⠂⢂⠸⣿⡄⠀⠈⣠⣴⣿⣿
// ⢐⠔⠠⠀⠀⡐⠠⢈⠢⢑⠄⠑⢈⠊⡂⡱⢁⣂⢌⢔⢌⢄⠀⠹⢀⣺⡿⣟⢿⣿⣿
// ⢀⠡⠁⠂⠐⠠⠈⠄⢈⠠⢈⢢⡣⣗⠕⠄⣕⢮⣞⣞⣗⣯⢯⡷⡴⣹⡪⣷⣿⣿⣿
// ⠊⠄⠠⠠⠡⠈⠠⢐⠠⡊⡎⣗⢭⢐⠹⡹⣮⡳⡵⣳⣻⢾⣻⣽⣻⣺⣺⣽⣿⣿⣿
// ⣨⣾⢐⠰⠐⠅⡂⡂⢕⢜⢜⢵⢹⢑⢔⠨⢘⠸⡹⡵⣯⣻⢽⣳⣻⣺⢞⡿⣿⣿⣿
// ⣿⣿⡔⠠⢈⠐⠐⢠⢱⢸⢸⢸⢸⠰⡡⢘⢔⢕⠝⢮⣳⢽⢝⡾⡵⡯⣏⠯⣿⣿⣿
// ⣿⣿⣗⢅⢢⠠⠡⠢⡱⡑⡕⡕⢅⠣⡊⢨⢪⡣⡣⡂⡬⡳⢽⢽⢽⢽⣞⣧⠙⣿⣿
// ⡻⣿⡯⡪⠢⡡⠡⢑⢌⠪⡪⡊⠆⢌⠪⢐⢕⢱⢱⢱⢱⢱⢙⢮⡫⡟⣞⢮⣳⠙⣿
// ⠊⣿⣯⠪⡊⠄⢅⠂⢂⠁⢇⢇⢃⠂⢕⠐⠌⡲⡰⡡⣇⠇⢇⢕⠪⠉⠂⠅⠂⡑⠹
// ⣸⢿⣳⢱⠨⡐⡽⡿⡶⡾⡬⡢⢂⠅⡢⢡⣌⠐⠈⢎⢎⢎⢔⠠⠡⠠⠠⠡⡁⡂⠡
// ⡯⡯⡇⢅⠕⠠⢱⢹⡙⢮⢹⠨⡂⡂⢇⠌⠮⡳⠅⡂⢕⠡⡑⠠⢁⢁⣡⣡⣢⣶⣿
// ⣗⢽⢌⡢⡡⡡⡸⡢⡣⡣⡱⡑⠔⡈⢎⢆⢂⠂⠅⣢⡳⣽⡐⢅⢂⣊⣿⣿⣿⣿⣿
// ⣯⢯⢷⢽⢮⢯⣺⣪⢞⡮⣳⢘⠔⢌⢜⣞⣖⣮⣻⢮⣯⢷⣿⣻⣿⣿⣿⣿⣿⣿⣿
// ⣿⣿⣿⣿⣿⣿⣷⣿⣿⣿⣿⣿⣿⣿⣿⣾⣷⣿⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿

type Figure = object

interface Field {
  coordinateX: number;
  coordinateY: number;
  fieldType: FieldType;
  whosHere: Figure | null;
  value: number;
}

interface Board {
  fields: Field[][];
  x: number;
  y: number;
}

function App() {
  const [board, setBoard] = useState<Board | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/game/board')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data: Board) => {
        setBoard(data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch board:", err);
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading board...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!board) return <div>No board data</div>;

  return (
    <div className="app-container">
      <h1>ChessR Battler Board</h1>
      <div className="board-container">
        <div className="board">
          {/* fields is Field[x][y], so iterating gives us columns */}
          {board.fields.map((column, colIndex) => (
            <div key={`col-${colIndex}`} className="board-column">
              {column.map((field, rowIndex) => (
                <div 
                  key={`field-${field.coordinateX}-${field.coordinateY}`} 
                  className={`board-field ${(colIndex + rowIndex) % 2 === 0 ? 'light' : 'dark'} ${field.fieldType.toLowerCase().replace('_', '-')}`}
                  title={`X:${field.coordinateX}, Y:${field.coordinateY} Type:${field.fieldType}`}
                >
                  {field.fieldType !== FieldType.NORMAL && field.value}
                  {field.whosHere && <span>FIG</span>}
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default App
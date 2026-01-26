import { useState, useEffect } from 'react'
import './App.css'
import circleWhite from './assets/svg-white/circle.svg'
import circleBlack from './assets/svg-black/circle.svg'
import hexWhite from './assets/svg-white/hexagon.svg'
import hexBlack from './assets/svg-black/hexagon.svg'
import squareWhite from './assets/svg-white/square.svg'
import squareBlack from './assets/svg-black/square.svg'
import starWhite from './assets/svg-white/star.svg'
import starBlack from './assets/svg-black/star.svg'
import triangleWhite from './assets/svg-white/triangle.svg'
import triangleBlack from './assets/svg-black/triangle.svg'

import plusWhite from './assets/svg-white/plus.svg'
import plusBlack from './assets/svg-black/plus.svg'


// Define types based on backend structure
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
enum FieldType {
  NORMAL = "NORMAL",
  MANA = "MANA",
  HP_FIGURE = "HP_FIGURE",
  HP_BASE = "HP_BASE"
}

type FigureType = 'CIRCLE' | 'HEXAGON' | 'PLUS' | 'SQUARE' | 'STAR' | 'TRIANGLE';
type FigureColor = 'WHITE' | 'BLACK';

interface Figure {
  type: FigureType;
  color: FigureColor;
  health: number;
  damage: number;
  cost: number;
  maxHealth: number;
}

interface UnitDefinition {
  id: string;
  name: string;
  type: FigureType;
  color: FigureColor;
  cost: number;
  health: number;
  damage: number;
}

interface Field {
  coordinateX: number;
  coordinateY: number;
  fieldType: FieldType;
  whosHere: Figure | null;
  value: number;
  possibleMoves: { x: number, y: number }[] | null;
}

interface Board {
  fields: Field[][];
  x: number;
  y: number;
}

interface PlayerState {
  name: string;
  mana: number;
  maxMana: number;
}

const FIGURE_ICONS: Record<FigureColor, Record<FigureType, string>> = {
  WHITE: {
    CIRCLE: circleWhite,
    HEXAGON: hexWhite,
    PLUS: plusWhite,
    SQUARE: squareWhite,
    STAR: starWhite,
    TRIANGLE: triangleWhite,
  },
  BLACK: {
    CIRCLE: circleBlack,
    HEXAGON: hexBlack,
    PLUS: plusBlack,
    SQUARE: squareBlack,
    STAR: starBlack,
    TRIANGLE: triangleBlack,
  }
};

const AVAILABLE_UNITS: UnitDefinition[] = [
  // WHITE UNITS
  { id: 'w-pawn', name: 'Pawn', type: 'CIRCLE', color: 'WHITE', cost: 1, health: 10, damage: 2 },
  { id: 'w-knight', name: 'Knight', type: 'TRIANGLE', color: 'WHITE', cost: 3, health: 25, damage: 5 },
  { id: 'w-bishop', name: 'Bishop', type: 'HEXAGON', color: 'WHITE', cost: 3, health: 20, damage: 6 },
  { id: 'w-rook', name: 'Rook', type: 'SQUARE', color: 'WHITE', cost: 5, health: 40, damage: 4 },
  { id: 'w-queen', name: 'Queen', type: 'STAR', color: 'WHITE', cost: 9, health: 35, damage: 10 },
  { id: 'w-king', name: 'King', type: 'PLUS', color: 'WHITE', cost: 0, health: 50, damage: 1 },

  // BLACK UNITS
  { id: 'b-pawn', name: 'Pawn', type: 'CIRCLE', color: 'BLACK', cost: 1, health: 10, damage: 2 },
  { id: 'b-knight', name: 'Knight', type: 'TRIANGLE', color: 'BLACK', cost: 3, health: 25, damage: 5 },
  { id: 'b-bishop', name: 'Bishop', type: 'HEXAGON', color: 'BLACK', cost: 3, health: 20, damage: 6 },
  { id: 'b-rook', name: 'Rook', type: 'SQUARE', color: 'BLACK', cost: 5, health: 40, damage: 4 },
  { id: 'b-queen', name: 'Queen', type: 'STAR', color: 'BLACK', cost: 9, health: 35, damage: 10 },
  { id: 'b-king', name: 'King', type: 'PLUS', color: 'BLACK', cost: 0, health: 50, damage: 1 },
];

function App() {
  const [board, setBoard] = useState<Board | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedField, setSelectedField] = useState<Field | null>(null);
  
  // Mock player state
  const [player1] = useState<PlayerState>({ name: 'Player 1', mana: 3, maxMana: 10 });
  const [player2] = useState<PlayerState>({ name: 'Player 2', mana: 3, maxMana: 10 });

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

  const handleFieldClick = (field: Field) => {
    // If a figure is already selected, try to move it
    if (selectedField && selectedField.whosHere) {
      const canMove = selectedField.possibleMoves?.some(
        m => m.x === field.coordinateX && m.y === field.coordinateY
      );

      if (canMove) {
        fetch(`http://localhost:8080/api/game/move/${selectedField.coordinateX}/${selectedField.coordinateY}/${field.coordinateX}/${field.coordinateY}`, {
          method: 'POST'
        })
          .then(res => res.json())
          .then((data: Board) => {
            setBoard(data);
            setSelectedField(null);
          })
          .catch(err => console.error("Move failed:", err));
        return;
      }
    }

    // Otherwise, select the field if it has a figure
    if (field.whosHere) {
      setSelectedField(field);
    } else {
      setSelectedField(null);
    }
  };

  const isHighlighted = (x: number, y: number) => {
    return selectedField?.possibleMoves?.some(m => m.x === x && m.y === y);
  };

  const handleDragStart = (event: React.DragEvent<HTMLDivElement>, unit: UnitDefinition) => {
    event.dataTransfer.setData('unit', JSON.stringify(unit));
    event.dataTransfer.effectAllowed = 'copy';
  };

  const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault(); // Necessary to allow dropping
    event.dataTransfer.dropEffect = 'copy';
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>, targetX: number, targetY: number) => {
    event.preventDefault();
    const unitData = event.dataTransfer.getData('unit');
    
    if (unitData && board) {
      const unit: UnitDefinition = JSON.parse(unitData);
      
      fetch(`http://localhost:8080/api/game/place/${unit.type}/${unit.color}/${targetX}/${targetY}`, {
        method: 'POST'
      })
        .then(res => res.json())
        .then((data: Board) => {
          setBoard(data);
        })
        .catch(err => console.error("Placement failed:", err));
    }
  };

  const renderPlayerInfo = (player: PlayerState, isTop: boolean) => (
    <div className={`player-info ${isTop ? 'player-top' : 'player-bottom'}`}>
      <div className="player-name">{player.name}</div>
      <div className="mana-container">
        <div className="mana-label">Mana: {player.mana} / {player.maxMana}</div>
        <div className="mana-bar-bg">
          <div 
            className="mana-bar-fill" 
            style={{ width: `${(player.mana / player.maxMana) * 100}%` }}
          ></div>
          <div className="mana-pips">
            {Array.from({ length: player.maxMana - 1 }).map((_, i) => (
              <div key={i} className="mana-pip" style={{ left: `${((i + 1) / player.maxMana) * 100}%` }}></div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );

  const renderUnitList = (color: FigureColor) => {
    return (
      <div className={`unit-panel ${color.toLowerCase()}-panel`}>
        <h2>{color} Units</h2>
        <div className="unit-list">
          {AVAILABLE_UNITS.filter(u => u.color === color).map(unit => (
            <div 
              key={unit.id} 
              className="unit-card"
              draggable
              onDragStart={(e) => handleDragStart(e, unit)}
            >
              <div className="unit-icon">
                <img src={FIGURE_ICONS[unit.color][unit.type]} alt={unit.name} />
              </div>
              <div className="unit-details">
                <h3>{unit.name}</h3>
                <div className="unit-stats">
                  <span title="Mana Cost">💧 {unit.cost}</span>
                  <span title="Health">❤️ {unit.health}</span>
                  <span title="Damage">⚔️ {unit.damage}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  if (loading) return <div>Loading board...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!board) return <div>No board data</div>;

  return (
    <div className="app-container">
      {renderUnitList('WHITE')}
      
      <div className="game-area">
        <h1>ChessR Battler</h1>
        
        {renderPlayerInfo(player2, true)}

        <div className="board-container">
          <div className="board">
            {/* fields is Field[x][y], so iterating gives us columns */}
            {board.fields.map((column, colIndex) => (
              <div key={`col-${colIndex}`} className="board-column">
                {column.map((field, rowIndex) => (
                  <div 
                    key={`field-${field.coordinateX}-${field.coordinateY}`} 
                    className={`board-field 
                      ${(colIndex + rowIndex) % 2 === 0 ? 'light' : 'dark'} 
                      ${field.fieldType.toLowerCase().replace('_', '-')}
                      ${selectedField === field ? 'selected' : ''}
                      ${isHighlighted(field.coordinateX, field.coordinateY) ? 'highlighted' : ''}
                    `}
                    title={`X:${field.coordinateX}, Y:${field.coordinateY} Type:${field.fieldType}`}
                    onDragOver={handleDragOver}
                    onDrop={(e) => handleDrop(e, field.coordinateX, field.coordinateY)}
                    onClick={() => handleFieldClick(field)}
                  >
                    {field.fieldType !== FieldType.NORMAL && field.value}
                    {field.whosHere && (
                      <img 
                        src={FIGURE_ICONS[field.whosHere.color][field.whosHere.type]} 
                        alt={`${field.whosHere.color} ${field.whosHere.type}`}
                        style={{ width: '80%', height: '80%' }}
                      />
                    )}
                  </div>
                ))}
              </div>
            ))}
          </div>
        </div>

        {renderPlayerInfo(player1, false)}

      </div>

      {renderUnitList('BLACK')}
    </div>
  )
}

export default App
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
  hp: number;
  maxHp: number;
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

const FIGURE_NAMES: Record<FigureType, string> = {
  CIRCLE: 'Pawn',
  TRIANGLE: 'Knight',
  HEXAGON: 'Bishop',
  SQUARE: 'Rook',
  STAR: 'Queen',
  PLUS: 'King'
};

function App() {
  const [board, setBoard] = useState<Board | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedField, setSelectedField] = useState<Field | null>(null);
  const [availableUnits, setAvailableUnits] = useState<UnitDefinition[]>([]);
  
  // Mock player state
  const [player1] = useState<PlayerState>({ name: 'Player 1', mana: 3, maxMana: 10, hp: 10, maxHp: 10 });
  const [player2] = useState<PlayerState>({ name: 'Player 2', mana: 3, maxMana: 10, hp: 10, maxHp: 10 });

  useEffect(() => {
    // Fetch board
    const fetchBoard = fetch('http://localhost:8080/api/game/board')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      });

    // Fetch figures
    const fetchFigures = fetch('http://localhost:8080/api/game/figures')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      });

    Promise.all([fetchBoard, fetchFigures])
      .then(([boardData, figuresData]: [Board, Figure[]]) => {
        setBoard(boardData);
        
        // Map backend figures to frontend UnitDefinition
        const units: UnitDefinition[] = figuresData.map((f, index) => ({
          id: `${f.color.toLowerCase()}-${f.type.toLowerCase()}-${index}`,
          name: FIGURE_NAMES[f.type] || f.type,
          type: f.type,
          color: f.color,
          cost: f.cost,
          health: f.health,
          damage: f.damage
        }));
        setAvailableUnits(units);
        
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch data:", err);
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

  const isPlacementValid = (unit: UnitDefinition, y: number) => {
    if (unit.color === 'WHITE') {
      return y >= 7 && y <= 9;
    } else if (unit.color === 'BLACK') {
      return y >= 0 && y <= 2;
    }
    return false;
  };

  const [draggedUnit, setDraggedUnit] = useState<UnitDefinition | null>(null);

  const handleDragStart = (event: React.DragEvent<HTMLDivElement>, unit: UnitDefinition) => {
    setDraggedUnit(unit);
    event.dataTransfer.setData('unit', JSON.stringify(unit));
    event.dataTransfer.effectAllowed = 'copy';
  };

  const handleDragEnd = () => {
    setDraggedUnit(null);
  };

  const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault(); // Necessary to allow dropping
    event.dataTransfer.dropEffect = 'copy';
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>, targetX: number, targetY: number) => {
    event.preventDefault();
    setDraggedUnit(null);
    const unitData = event.dataTransfer.getData('unit');
    
    if (unitData && board) {
      const unit: UnitDefinition = JSON.parse(unitData);
      
      // Validation of placement zones
      if (unit.color === 'WHITE') {
        if (targetY < 7 || targetY > 9) {
          alert("Białe figury można ustawiać tylko na polach y7-y9");
          return;
        }
      } else if (unit.color === 'BLACK') {
        if (targetY < 0 || targetY > 2) {
          alert("Czarne figury można ustawiać tylko na polach y0-y2");
          return;
        }
      }
      
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
      <div className="player-stats-container">
        <div className="hp-label" style={{ color: '#ff5555', fontWeight: 'bold', marginRight: '15px' }}>
          HP: {player.hp} / {player.maxHp}
        </div>
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
    </div>
  );

  const renderUnitList = (color: FigureColor) => {
    return (
      <div className={`unit-panel ${color.toLowerCase()}-panel`}>
        <h2>{color} Units</h2>
        <div className="unit-list">
          {availableUnits.filter(u => u.color === color).map(unit => (
            <div 
              key={unit.id} 
              className="unit-card"
              draggable
              onDragStart={(e) => handleDragStart(e, unit)}
              onDragEnd={handleDragEnd}
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
                      ${draggedUnit && isPlacementValid(draggedUnit, field.coordinateY) ? 'valid-drop' : ''}
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
import { useState, useEffect } from 'react'
import toast, { Toaster } from 'react-hot-toast';
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

interface GameState {
    board: Board;
    player1: PlayerState;
    player2: PlayerState;
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

type FigureType = 'CIRCLE' | 'HEXAGON' | 'PLUS' | 'SQUARE' | 'STAR' | 'TRIANGLE';
type FigureColor = 'WHITE' | 'BLACK';


function App() {
    const [gameState, setGameState] = useState<GameState | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedField, setSelectedField] = useState<Field | null>(null);
    const [availableUnits, setAvailableUnits] = useState<UnitDefinition[]>([]);
    const [winner, setWinner] = useState<string | null>(null);

    useEffect(() => {
        const fetchGameState = fetch('http://localhost:8080/api/game/state')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            });

        const fetchFigures = fetch('http://localhost:8080/api/game/figures')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            });

        Promise.all([fetchGameState, fetchFigures])
            .then(([gameStateData, figuresData]: [GameState, Figure[]]) => {
                setGameState(gameStateData);

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

    useEffect(() => {
        if (gameState) {
            if (gameState.player1.hp <= 0) {
                setWinner('BLACK');
            } else if (gameState.player2.hp <= 0) {
                setWinner('WHITE');
            }
        }
    }, [gameState]);

    const handleFieldClick = (field: Field) => {
        if (winner) return; // Don't allow moves if the game is over
        if (selectedField && selectedField.whosHere) {
            const canMove = selectedField.possibleMoves?.some(
                m => m.x === field.coordinateX && m.y === field.coordinateY
            );

            if (canMove) {
                fetch(`http://localhost:8080/api/game/move/${selectedField.coordinateX}/${selectedField.coordinateY}/${field.coordinateX}/${field.coordinateY}`, {
                    method: 'POST'
                })
                    .then(res => {
                        if (!res.ok) {
                            return res.text().then(text => { throw new Error(text) });
                        }
                        return res.json();
                    })
                    .then((data: GameState) => {
                        setGameState(data);
                        setSelectedField(null);
                    })
                    .catch(err => {
                        toast.error(err.message);
                    });
                return;
            }
        }

        if (field.whosHere) {
            setSelectedField(field);
        } else {
            setSelectedField(null);
        }
    };

    const isHighlighted = (x: number, y: number) => {
        if (winner) return false;
        return selectedField?.possibleMoves?.some(m => m.x === x && m.y === y);
    };

    const isPlacementValid = (unit: UnitDefinition, y: number) => {
        if (winner) return false;
        if (unit.color === 'WHITE') {
            return y >= 7 && y <= 9;
        } else if (unit.color === 'BLACK') {
            return y >= 0 && y <= 2;
        }
        return false;
    };

    const [draggedUnit, setDraggedUnit] = useState<UnitDefinition | null>(null);

    const handleDragStart = (event: React.DragEvent<HTMLDivElement>, unit: UnitDefinition) => {
        if (winner) {
            event.preventDefault();
            return;
        }
        setDraggedUnit(unit);
        event.dataTransfer.setData('unit', JSON.stringify(unit));
        event.dataTransfer.effectAllowed = 'copy';
    };

    const handleDragEnd = () => {
        setDraggedUnit(null);
    };

    const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
        if (winner) return;
        event.preventDefault();
        event.dataTransfer.dropEffect = 'copy';
    };

    const handleDrop = (event: React.DragEvent<HTMLDivElement>, targetX: number, targetY: number) => {
        if (winner) return;
        event.preventDefault();
        setDraggedUnit(null);
        const unitData = event.dataTransfer.getData('unit');

        if (unitData && gameState) {
            const unit: UnitDefinition = JSON.parse(unitData);

            fetch(`http://localhost:8080/api/game/place/${unit.type}/${unit.color}/${targetX}/${targetY}`, {
                method: 'POST'
            })
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(text => { throw new Error(text) });
                    }
                    return res.json();
                })
                .then((data: GameState) => {
                    setGameState(data);
                })
                .catch(err => {
                    toast.error(err.message);
                });
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

    const GameOverOverlay = ({ winner, onRestart }: { winner: string, onRestart: () => void }) => (
        <div className="game-over-overlay">
            <div className="game-over-content">
                <h2>Game Over</h2>
                <p>{winner} wins!</p>
                <button onClick={onRestart}>Play Again</button>
            </div>
        </div>
    );

    if (loading) return <div>Loading game...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!gameState) return <div>No game data</div>;

    return (
        <div className="app-container">
            {winner && <GameOverOverlay winner={winner} onRestart={() => window.location.reload()} />}
            <Toaster
                toastOptions={{
                    style: {
                        background: '#333',
                        color: '#fff',
                    },
                }}
            />
            {renderUnitList('WHITE')}

            <div className="game-area">
                <h1>ChessR Battler</h1>

                {renderPlayerInfo(gameState.player2, true)}

                <div className="board-container">
                    <div className="board">
                        {gameState.board.fields.map((column, colIndex) => (
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
                                            <>
                                                <img
                                                    src={FIGURE_ICONS[field.whosHere.color][field.whosHere.type]}
                                                    alt={`${field.whosHere.color} ${field.whosHere.type}`}
                                                    style={{ width: '80%', height: '80%' }}
                                                />
                                                <div className="figure-health">{field.whosHere.health}</div>
                                                <div className="figure-damage">{field.whosHere.damage}</div>
                                            </>
                                        )}
                                    </div>
                                ))}
                            </div>
                        ))}
                    </div>
                </div>

                {renderPlayerInfo(gameState.player1, false)}

            </div>

            {renderUnitList('BLACK')}
        </div>
    )
}


export default App